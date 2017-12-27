/**
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.inren.service.usersettings;

import net.bull.javamelody.MonitoredWithSpring;
import de.inren.data.domain.usersettings.UserSettings;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
@MonitoredWithSpring
public interface UserSettingsService extends Initializable {

    UserSettings load(long id);

    UserSettings loadByUser(Long uid);

    UserSettings save(UserSettings userSettings);

    void deleteUserSettings(UserSettings userSettings);

}
