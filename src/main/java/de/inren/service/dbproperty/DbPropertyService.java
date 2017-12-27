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

import net.bull.javamelody.MonitoredWithSpring;
import de.inren.data.domain.dbproperty.DbProperty;
import de.inren.service.Initializable;

/**
 * @author Ingo Renner
 * 
 */
@MonitoredWithSpring
public interface DbPropertyService extends Initializable {

    String getValue(String name, String host);

    String getValue(String name);

    DbProperty saveDefault(String name, String value, String description);

    DbProperty saveLocal(String name, String value, String description);

    DbProperty save(DbProperty entity);

    void delete(DbProperty entity);

    Iterable<DbProperty> loadDbProperties();

}
