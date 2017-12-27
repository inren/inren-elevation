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
package de.inren.service.user;

import java.util.List;

import net.bull.javamelody.MonitoredWithSpring;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import de.inren.data.domain.user.User;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 *
 */
@MonitoredWithSpring
public interface UserService extends UserDetailsService, Initializable {

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserDetails loadByUsername(String username) throws UsernameNotFoundException;

    User loadByIdent(String ident);

    User save(User user);

    User authenticateUser(String ident, String password);

    List<User> loadAllUser();

    User loadUserByEmail(String email);

    List<User> search(User u);

}
