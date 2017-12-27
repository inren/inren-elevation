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
package de.inren.service.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.security.Right;
import de.inren.data.domain.security.Role;
import de.inren.data.repositories.security.RoleRepository;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * @author Ingo Renner
 * 
 */
@Service(value = "roleService")
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final static Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Resource
    RoleRepository roleRepository;

    @Autowired
    RightService rightService;

    private boolean initDone = false;

    @Override
    public void init() {
        if (!initDone) {
            log.info("RoleService init start.");
            rightService.init();
            List<Role> roles = (List<Role>) roleRepository.findAll();
            if (roles.isEmpty()) {
                List<Right> rights = rightService.loadAllRights();
                for (Roles role : Roles.values()) {
                    Role myRole = new Role(role.name(), role.name());
                    myRole.setRights(rights);
                    roleRepository.save(myRole);
                }
            }
            initDone = true;
            log.info("RoleService init done.");
        }
    }

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public List<Role> loadAllRoles() {
        List<Role> res = new ArrayList<Role>();
        CollectionUtils.addAll(res, roleRepository.findAll().iterator());
        return res;
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

}
