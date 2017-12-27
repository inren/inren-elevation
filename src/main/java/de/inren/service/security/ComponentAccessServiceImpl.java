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
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.security.ComponentAccess;
import de.inren.data.domain.security.Right;
import de.inren.data.domain.security.Role;
import de.inren.data.repositories.security.ComponentAccessRepository;
import de.inren.frontend.common.templates.SecuredPage;
import de.inren.service.security.RightService.Rights;
import de.inren.service.security.RoleService.Roles;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * @author Ingo Renner
 *
 */
@Service(value = "componentAccessService")
@Transactional(readOnly = true)
public class ComponentAccessServiceImpl implements ComponentAccessService {

    private final static Logger       log      = LoggerFactory.getLogger(ComponentAccessServiceImpl.class);

    @Autowired
    RoleService                       roleService;

    @Autowired
    RightService                      rightService;

    @Autowired
    private ComponentAccessRepository componentAccessRepository;

    private boolean                   initDone = false;

    private BeanDefinitionRegistry    beanDefinitionRegistry;

    private void initBeanDefinitionRegistry() {
        beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
        ClassPathBeanDefinitionScanner beanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanDefinitionRegistry);

        TypeFilter typeFilter = new AssignableTypeFilter(SecuredPage.class);
        beanDefinitionScanner.addIncludeFilter(typeFilter);
        beanDefinitionScanner.setIncludeAnnotationConfig(false);
        beanDefinitionScanner.scan("de.inren.frontend");
    }

    @Override
    @CacheEvict("ComponentAccess")
    public void init() {
        if (!initDone) {
            log.info("ComponentAccessService init start.");
            roleService.init();
            initBeanDefinitionRegistry();
            List<String> components = Arrays.asList(findPages());

            if (log.isDebugEnabled()) {
                log.debug("Pages = " + (components == null ? "no pages found." : components));
            }

            if (components != null) {
                final Role securedPage = getOrCreateSecuredPageRole();
                for (String page : components) {
                    try {
                        BeanDefinition bd = beanDefinitionRegistry.getBeanDefinition(page);
                        if (log.isDebugEnabled()) {
                            log.debug("found page: " + page + " Definition: " + bd.toString());
                        }

                        if (componentAccessRepository.findComponentAccessByName(page) == null) {
                            // create entry for new component
                            ComponentAccess componentAccess = new ComponentAccess();
                            componentAccess.setName(page);
                            componentAccess.getRoles().add(securedPage);
                            componentAccess.getRoles().add(getOrCreatecomponentRole(page));
                            componentAccessRepository.save(componentAccess);
                        }
                    } catch (Exception e) {
                        log.error("failed to initialize service", e);
                    }
                }
            }
            initDone = true;
            log.info("ComponentAccessService init done.");
        }

    }

    private Role getOrCreatecomponentRole(String page) {
        final String pageName = "ROLE_" + page;
        Role role = roleService.findRoleByName(pageName);
        if (role == null) {
            role = new Role(pageName, pageName);
            Right enable = rightService.findRightByName(Rights.ENABLE.name());
            Right render = rightService.findRightByName(Rights.RENDER.name());
            role.getRights().add(enable);
            role.getRights().add(render);
            role = roleService.save(role);
        }
        return role;
    }

    private Role getOrCreateSecuredPageRole() {
        Role role = roleService.findRoleByName(Roles.ROLE_SECURED_PAGE.name());
        if (role == null) {
            role = new Role(Roles.ROLE_SECURED_PAGE.name(), Roles.ROLE_SECURED_PAGE.name());
            Right enable = rightService.findRightByName(Rights.ENABLE.name());
            Right render = rightService.findRightByName(Rights.RENDER.name());
            role.getRights().add(enable);
            role.getRights().add(render);
            role = roleService.save(role);
        }
        return role;
    }

    private String[] findPages() {
        String[] beans = beanDefinitionRegistry.getBeanDefinitionNames();
        return beans;
    }

    @Override
    @CacheEvict("ComponentAccess")
    public ComponentAccess save(ComponentAccess componentAccess) {
        return componentAccessRepository.save(componentAccess);
    }

    @Override
    @Cacheable("ComponentAccess")
    public List<ComponentAccess> getComponentAccessList() {
        List<ComponentAccess> res = new ArrayList<ComponentAccess>();
        Iterable<ComponentAccess> all = componentAccessRepository.findAll();
        for (ComponentAccess componentAccess : all) {
            res.add(componentAccess);
        }

        return res;
    }

    @Override
    @Cacheable("ComponentAccess")
    public ComponentAccess findComponentAccessByName(String name) {
        return componentAccessRepository.findComponentAccessByName(name);
    }
}
