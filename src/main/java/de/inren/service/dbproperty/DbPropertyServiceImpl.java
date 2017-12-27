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

package de.inren.service.dbproperty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import de.inren.data.domain.dbproperty.DbProperty;
import de.inren.data.repositories.dbproperty.DbPropertyRepository;

/**
 * @author Ingo Renner
 * 
 */
@SuppressWarnings("serial")
@Service(value = "dbPropertyService")
@Transactional(readOnly = true)
public class DbPropertyServiceImpl implements DbPropertyService {

    private final static Logger log = LoggerFactory.getLogger(DbPropertyServiceImpl.class);

    @Autowired
    private DbPropertyRepository dbPropertyRepository;

    
    @Override
    public void init() {
        log.info("DbPropertyService initialized.");
    }

    @Override
    public String getValue(String name, String host) {
        DbProperty res = dbPropertyRepository.findByNameAndHost(name, host);
        return res != null ? res.getValue() : null;
    }

    @Override
    public String getValue(String name) {
        String res = getValue(name, getHost());
        if (res != null) {
            return res;
        }
        List<DbProperty> list = dbPropertyRepository.findByName(name);
        if (list == null || list.isEmpty()) {
            return null;
        }
        for (DbProperty dbProperty : list) {
            if (StringUtils.isEmpty(dbProperty.getHost())) {
                return dbProperty.getValue();
            }
        }
        return null;
    }

    @Override
    public DbProperty saveDefault(String name, String value, String description) {
        return save(name, null, value, description);
    }

    @Override
    public DbProperty saveLocal(String name, String value, String description) {
        return save(name, getHost(), value, description);
    }

    @Override
    public DbProperty save(DbProperty entity) {
        return dbPropertyRepository.save(entity);
    }

    @Override
    public void delete(DbProperty entity) {
        dbPropertyRepository.delete(entity);
    }

    private DbProperty save(String name, String host, String value, String description) {
        DbProperty res = dbPropertyRepository.findByNameAndHost(name, host);
        if (res == null) {
            return dbPropertyRepository.save(new DbProperty(name, host, value, description));
        } else {
            res.setValue(value);
            res.setDescription(description);
            return dbPropertyRepository.save(res);
        }
    }

    @Override
    public Iterable<DbProperty> loadDbProperties() {
        return dbPropertyRepository.findAll();
    }

    private String getHost() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
