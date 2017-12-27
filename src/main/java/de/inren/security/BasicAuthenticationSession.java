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
package de.inren.security;

import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import de.inren.data.domain.user.User;
import de.inren.data.domain.usersettings.UserSettings;
import de.inren.service.authentication.AuthenticationService;
import de.inren.service.usersettings.UserSettingsService;

/**
 * @author Ingo Renner
 * 
 */
public class BasicAuthenticationSession extends AuthenticatedWebSession {
    private final static Logger   log = LoggerFactory.getLogger(BasicAuthenticationSession.class);

    @SpringBean
    private UserSettingsService   userSettingsService;

    @SpringBean
    private AuthenticationService authenticationService;

    private User                  user;

    private UserSettings          userSettings;

    public User getUser() {
        return user;
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public BasicAuthenticationSession(Request request) {
        super(request);
        Injector.get().inject(this);
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            this.user = authenticationService.authenticateUser(username, password);
            if (user != null) {
                this.userSettings = userSettingsService.loadByUser(user.getId());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("authentication failed", e);
        }
        return false;
    }

    @Override
    public Roles getRoles() {
        Roles resultRoles = new Roles();
        if (user != null) {
            for (SimpleGrantedAuthority authority : user.getAuthorities()) {
                resultRoles.add(authority.getAuthority());
            }
        }
        return resultRoles;
    }

    @Override
    public void signOut() {
        super.signOut();
        user = null;
        userSettings = null;
    }

    public static BasicAuthenticationSession get() {
        return (BasicAuthenticationSession) Session.get();
    }
}