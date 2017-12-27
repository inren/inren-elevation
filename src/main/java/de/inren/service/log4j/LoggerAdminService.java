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
package de.inren.service.log4j;

import java.util.List;

import net.bull.javamelody.MonitoredWithSpring;

import org.apache.log4j.Logger;

import de.inren.data.domain.loggerbean.LoggerBean;
import de.inren.service.Initializable;

@MonitoredWithSpring
public interface LoggerAdminService extends Initializable {

    /**
     * Get list of references for all logger, available by this service.
     * 
     * @return List of Loggers never null
     */
    List<LoggerBean> getAllLogger();

    void reInit();

    List<String> getLogLevels();

    void setLevel(Logger logger, String level);

    void setLevelAndSave(String logger, String level);
}
