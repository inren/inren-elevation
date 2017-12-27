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

import javax.annotation.Resource;

import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme;
import de.inren.data.domain.user.User;
import de.inren.data.domain.usersettings.UserSettings;
import de.inren.data.repositories.usersettings.UserSettingsRepository;
import de.inren.service.user.UserService;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "userSettingsService")
@Transactional(readOnly = true)
public class UserSettingsServiceImpl implements UserSettingsService {

    private static final Logger log = LoggerFactory.getLogger(UserSettingsServiceImpl.class);

    @Resource
    UserSettingsRepository      userSettingsRepository;

    @Autowired
    UserService                 userService;

    @Override
    public void init() {
        log.info("UserSettingsService init start.");
        userService.init();
        for (User user : userService.loadAllUser()) {
            UserSettings userSettings = userSettingsRepository.findByUid(user.getId());
            if (userSettings == null) {
                userSettings = new UserSettings();
                userSettings.setUid(user.getId());
                userSettings.setTheme(BootswatchTheme.Lumen.name());
                userSettingsRepository.save(userSettings);
            }
        }
        log.info("UserSettingsService init done.");
    }

    @Override
    public UserSettings load(long id) {
        return userSettingsRepository.findById(id).get();
    }

    @Override
    public UserSettings loadByUser(Long uid) {
        UserSettings us = userSettingsRepository.findByUid(uid);
        if (us == null) {
            us = createDefaultUserSettings(uid);
        }
        return us;
    }

    private UserSettings createDefaultUserSettings(Long uid) {
        try {
            Application.get();
        } catch (Exception e) {
            return null;
        }
        UserSettings us = new UserSettings();
        us.setUid(uid);
        us.setTheme(Bootstrap.getSettings(Application.get()).getThemeProvider().defaultTheme().name());
        save(us);
        return us;
    }

    @Override
    public UserSettings save(UserSettings userSettings) {
        try {
            return userSettingsRepository.save(userSettings);
        } catch (Exception e) {
            log.error("error saving userSettings: " + userSettings, e);
            throw new RuntimeException("UserSettingsService", e);
        }
    }

    @Override
    public void deleteUserSettings(UserSettings userSettings) {
        userSettingsRepository.delete(userSettings);
    }
}
