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

import UIKit
import BeagleUI
import BeagleSchema

struct PageViewScreen: DeeplinkScreen {
    init(path: String, data: [String: String]?) {
    }
    
    func screenController() -> UIViewController {
        return Beagle.screen(.declarative(screen))
    }
    
    var screen: Screen {
        return Screen(
            navigationBar: NavigationBar(title: "PageView"),
            child: PageView(
                pages: Array(repeating: Page(), count: 3).map { $0.content },
                pageIndicator: PageIndicator()
            )
        )
    }
}

struct Page {
    var content: Container {
        return Container(
            children: [
                Text("Text with alignment attribute set to center", alignment: .center),
                Text("Text with alignment attribute set to right", alignment: .right),
                Text("Text with alignment attribute set to left", alignment: .left),
                Image(.network(.NETWORK_IMAGE_BEAGLE))
            ],
            widgetProperties: .init(flex: Flex().justifyContent(.spaceBetween).grow(1))
        )
    }
}
