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
package de.inren.service.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.group.Group;
import de.inren.data.domain.security.Role;
import de.inren.data.repositories.group.GroupRepository;
import de.inren.service.security.RoleService;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * 
 * @author Ingo Renner
 * 
 */
@Service(value = "groupService")
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final static Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private boolean initDone = false;

    @Resource
    RoleService roleService;

    @Resource
    GroupRepository groupRepository;

    @Override
    public void init() {
        if (!initDone) {
            log.info("GroupService init start.");
            roleService.init();
            List<Group> groups = (List<Group>) groupRepository.findAll();
            if (groups.isEmpty()) {
                final Group all = new Group("all", "All roles");
                Set<Role> groupRoles = new HashSet<Role>();
                groupRoles.addAll(roleService.loadAllRoles());
                all.setRoles(groupRoles);
                groupRepository.save(all);
            }
            initDone = true;
            log.info("GroupService init done.");
        }
    }

    @Override
    public Group findGroupByName(String name) {
        return groupRepository.findGroupByName(name);
    }

    @Override
    public List<Group> loadAllGroups() {
        List<Group> res = new ArrayList<Group>();
        CollectionUtils.addAll(res, groupRepository.findAll().iterator());
        return res;
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

}
