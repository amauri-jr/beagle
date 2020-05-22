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

public struct Button: Widget, ClickedOnComponent, AutoInitiableAndDecodable {
    
    // MARK: - Public Properties
    public let text: String
    public let style: String?
    public let action: Action?
    public var clickAnalyticsEvent: AnalyticsClick?
    public var widgetProperties: WidgetProperties

// sourcery:inline:auto:Button.Init
    public init(
        text: String,
        style: String? = nil,
        action: Action? = nil,
        clickAnalyticsEvent: AnalyticsClick? = nil,
        widgetProperties: WidgetProperties = WidgetProperties()
    ) {
        self.text = text
        self.style = style
        self.action = action
        self.clickAnalyticsEvent = clickAnalyticsEvent
        self.widgetProperties = widgetProperties
    }
// sourcery:end
}

