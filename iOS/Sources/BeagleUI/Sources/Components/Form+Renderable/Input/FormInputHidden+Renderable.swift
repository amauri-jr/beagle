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
import BeagleSchema

extension FormInputHidden: ServerDrivenComponent {
    public func toView(renderer: BeagleRenderer) -> UIView {
        let view = HidenInputView(value: value)
        view.beagleElement = self
        view.flex.setup(Flex(positionType: .absolute))
        return view
    }
}

class HidenInputView: UIView, InputValue {
    
    let value: String
    
    init(value: String) {
        self.value = value
        super.init(frame: .zero)
        isHidden = true
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func getValue() -> Any {
        return value
    }
}
