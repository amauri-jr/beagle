/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle.android.components.layout

import android.view.View
import br.com.zup.beagle.analytics.ScreenEvent
import br.com.zup.beagle.android.setup.BeagleEnvironment
import br.com.zup.beagle.android.utils.ToolbarManager
import br.com.zup.beagle.android.utils.configureSupportActionBar
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.core.Style
import br.com.zup.beagle.widget.core.Flex
import br.com.zup.beagle.widget.layout.NavigationBar
import br.com.zup.beagle.widget.layout.SafeArea

internal data class ScreenComponent(
    val identifier: String? = null,
    private val safeArea: SafeArea? = null,
    val navigationBar: NavigationBar? = null,
    val child: ServerDrivenComponent,
    private val screenAnalyticsEvent: ScreenEvent? = null,
    override var style: Style? = null
) : WidgetView() {

    @Transient
    private val viewFactory: ViewFactory = ViewFactory()

    @Transient
    private val toolbarManager: ToolbarManager = ToolbarManager()

    override fun buildView(rootView: RootView): View {
        addNavigationBarIfNecessary(rootView, navigationBar)

        val container = viewFactory.makeBeagleFlexView(rootView.getContext(), Flex(grow = 1.0))

        container.addServerDrivenComponent(child, rootView)

        screenAnalyticsEvent?.let {
            container.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(v: View?) {
                    BeagleEnvironment.beagleSdk.analytics?.sendViewWillAppearEvent(it)
                }

                override fun onViewDetachedFromWindow(v: View?) {
                    BeagleEnvironment.beagleSdk.analytics?.sendViewWillDisappearEvent(it)
                }
            })
        }

        return container
    }

    private fun addNavigationBarIfNecessary(rootView: RootView, navigationBar: NavigationBar?) {
        (rootView.getContext() as? BeagleActivity)?.let {
            if (navigationBar != null) {
                configNavigationBar(rootView, navigationBar)
            } else {
                hideNavigationBar(it)
            }
        }
    }

    private fun hideNavigationBar(context: BeagleActivity) {
        context.supportActionBar?.apply {
            hide()
        }

        context.getToolbar().visibility = View.GONE
    }

    private fun configNavigationBar(rootView: RootView, navigationBar: NavigationBar) {
        (rootView.getContext() as? BeagleActivity)?.let {
            it.configureSupportActionBar()
            toolbarManager.configureNavigationBarForScreen(it, navigationBar)
            toolbarManager.configureToolbar(rootView, navigationBar)
        }
    }
}
