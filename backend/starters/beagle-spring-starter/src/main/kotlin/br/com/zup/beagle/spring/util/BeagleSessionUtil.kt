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

package br.com.zup.beagle.spring.util

import br.com.zup.beagle.enums.BeaglePlatform
import br.com.zup.beagle.utils.BeaglePlatformUtil
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

object BeagleSessionUtil {

    fun getBeaglePlatformFromSession(): BeaglePlatform {
        val currentPlatform = RequestContextHolder.currentRequestAttributes().getAttribute(
            BeaglePlatformUtil.BEAGLE_PLATFORM_HEADER,
            RequestAttributes.SCOPE_REQUEST
        )
        return if (currentPlatform != null) {
            BeaglePlatform.valueOf(currentPlatform as String)
        } else {
            BeaglePlatform.ALL
        }
    }

}