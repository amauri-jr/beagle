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

import android.content.Context
import android.view.View
import br.com.zup.beagle.android.view.BeagleFlexView
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.widget.core.ViewConvertable
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.widget.core.Flex
import br.com.zup.beagle.widget.layout.Container

data class Container(
    override val children: List<ServerDrivenComponent>
) : Container(children), ViewConvertable {

    private val viewFactory = ViewFactory()

    override fun buildView(context: Context): View {
        return viewFactory.makeBeagleFlexView(context, flex ?: Flex())
            .apply {
                addChildren(this)
            }
    }

    private fun addChildren(beagleFlexView: BeagleFlexView) {
        children.forEach { child ->
//            beagleFlexView.addServerDrivenComponent(child, rootView)
        }
    }
}
