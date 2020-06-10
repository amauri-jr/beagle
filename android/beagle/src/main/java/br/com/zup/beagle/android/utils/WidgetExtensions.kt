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

package br.com.zup.beagle.android.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import br.com.zup.beagle.android.components.layout.Container
import br.com.zup.beagle.android.components.layout.Screen
import br.com.zup.beagle.android.components.layout.ScreenComponent
import br.com.zup.beagle.android.engine.renderer.ActivityRootView
import br.com.zup.beagle.android.engine.renderer.FragmentRootView
import br.com.zup.beagle.android.engine.renderer.ViewRendererFactory
import br.com.zup.beagle.android.widget.core.RootView
import br.com.zup.beagle.core.LayoutComponent
import br.com.zup.beagle.core.ServerDrivenComponent

internal var viewRenderer = ViewRendererFactory()

fun ServerDrivenComponent.toView(context: Context) = this.toView(context as AppCompatActivity)

fun ServerDrivenComponent.toView(activity: AppCompatActivity) =
    this.toView(ActivityRootView(activity))

fun ServerDrivenComponent.toView(fragment: Fragment) = this.toView(FragmentRootView(fragment))

fun Screen.toView(activity: AppCompatActivity) = this.toComponent().toView(activity)

fun Screen.toView(fragment: Fragment) = this.toComponent().toView(fragment)

internal fun Screen.toComponent() = ScreenComponent(
    identifier = this.identifier,
    navigationBar = this.navigationBar,
    child = this.child,
    screenAnalyticsEvent = screenAnalyticsEvent,
    appearance = appearance
)

internal fun ServerDrivenComponent.toView(rootView: RootView) =
    if (this is LayoutComponent) {
        viewRenderer.make(this).build(rootView)
    } else {
        val container = Container(listOf(this))
        viewRenderer.make(container).build(rootView)
    }

internal fun br.com.zup.beagle.widget.layout.Screen.toAndroidScreen() = Screen(
    identifier = this.identifier,
    navigationBar = this.navigationBar,
    child = this.child,
    screenAnalyticsEvent = this.screenAnalyticsEvent,
    appearance = this.appearance
)