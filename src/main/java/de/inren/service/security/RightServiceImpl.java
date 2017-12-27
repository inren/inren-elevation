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

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.inren.data.domain.security.Right;
import de.inren.data.repositories.security.RightRepository;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * @author Ingo Renner
 * 
 */
@Service(value = "rightService")
@Transactional(readOnly = true)
public class RightServiceImpl implements RightService {
    private final static Logger log      = LoggerFactory.getLogger(RightServiceImpl.class);

    @Autowired
    RightRepository             rightRepository;

    private boolean             initDone = false;

    @Override
    public void init() {
        if (!initDone) {
            log.info("RightService init start.");
            List<Right> rights = (List<Right>) rightRepository.findAll();
            for (Rights defRight : Rights.values()) {
                if (!contains(rights, defRight)) {
                    rightRepository.save(new Right(defRight.name(), "A system default."));
                }
            }
            initDone = true;
            log.info("RightService init done.");
        }
    }

    private boolean contains(List<Right> rights, Rights defRight) {
        for (Right right : rights) {
            if (right.getName().equalsIgnoreCase(defRight.name())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Right findRightByName(String name) {
        return rightRepository.findRightByName(name);
    }

    @Override
    public List<Right> loadAllRights() {
        List<Right> res = new ArrayList<Right>();
        CollectionUtils.addAll(res, rightRepository.findAll().iterator());
        return res;
    }

    @Override
    public Right save(Right right) {
        return rightRepository.save(right);
    }

}
