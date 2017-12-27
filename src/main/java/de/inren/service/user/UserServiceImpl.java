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
import java.util.Optional;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.security.Role;
import de.inren.data.domain.user.User;
import de.inren.data.domain.user.UserSpecification;
import de.inren.data.repositories.user.UserRepository;
import de.inren.service.group.GroupService;
import de.inren.service.security.RoleService;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * @author Ingo Renner
 * 
 */
@Service(value = "userService")
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final static Logger log      = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserRepository              userRepository;

    @Autowired
    RoleService                 roleService;

    @Resource
    GroupService                groupService;

    @Autowired
    private PasswordEncoder     passwordEncoder;

    private boolean             initDone = false;

    @Override
    public void init() {
        if (!initDone) {
            log.info("UserService init start.");
            roleService.init();
            groupService.init();
            List<User> users = (List<User>) userRepository.findAll();
            List<Role> roles = roleService.loadAllRoles();

            if (users.isEmpty()) {
                log.info("UserService creating admin.");
                final User user = new User();
                user.setFirstname("admin");
                user.setLastname("admin");
                user.setEmail("admin@localhost");
                user.setPassword("geheim");
                user.setAccountNonExpired(true);
                user.setAccountNonLocked(true);
                user.setCredentialsNonExpired(true);
                user.setRoles(roles);
                save(user);
                log.info("created default user: " + user);
            }
            initDone = true;
            log.info("UserService init done.");
        }
    }

    @Override
    public User loadByIdent(String ident) {
        return (User) loadUserByUsername(ident);
    }

    @Override
    public UserDetails loadByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find any user by username = [" + username + "] .");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadByUsername(username);
    }

    @Override
    @Transactional
    public User save(User user) {
        try {
            if (user.getId() == null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                User u2 = userRepository.save(user);
                log.info("created new user: {}", u2);
                return u2;
            } else {
                final Optional<User> optionalUser = userRepository.findById(user.getId());
                final User u = optionalUser.get();
                if (user.getPassword() == null || "".equals(user.getPassword()) || user.getPassword().equals(u.getPassword())) {
                    user.setPassword(u.getPassword());
                    log.info("updating user: {}", user);
                } else {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    log.info("updating user with new password: {}", user);
                }
                return userRepository.save(user);
            }
        } catch (Exception e) {
            log.error("error saving user: " + user, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public User authenticateUser(String email, String password) {
        final User user = userRepository.findByEmail(email);
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    @Override
    public List<User> loadAllUser() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    public User loadUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> search(User userFilter) {
        UserSpecification us = new UserSpecification(userFilter);
        return (List<User>) userRepository.findAll(us);
    }
}
