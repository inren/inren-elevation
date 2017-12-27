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
/**
 * 
 */
package de.inren.service.log4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.wicket.util.string.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.inren.data.domain.loggerbean.LoggerBean;
import de.inren.data.repositories.loggerbean.LoggerBeanRepository;
import net.bull.javamelody.MonitoredWithSpring;

/**
 * Simple service to store and manage log4j logger configuration in db.
 * 
 * @author Ingo Renner
 *
 */
@Service(value = "log4jAdminService")
@MonitoredWithSpring
public class LoggerjAdminServiceImpl implements LoggerAdminService {

	private final static org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(LoggerjAdminServiceImpl.class);

	private boolean initDone = false;

	@Autowired
	LoggerBeanRepository loggerBeanRepository;

	@Override
	public void init() {
		if (!initDone) {
			log.info("LoggerAdminService init start.");

			int total = 0;
			int added = 0;
			int deleted = 0;

			Map<String, Logger> available = getAllLoggerInstances();
			total = available.size();

			List<LoggerBean> loaded = (List<LoggerBean>) loggerBeanRepository
					.findAll();
			Map<String, LoggerBean> configured = new HashMap<String, LoggerBean>();
			for (LoggerBean loggerBean : loaded) {
				configured.put(loggerBean.getName(), loggerBean);
			}

			for (String key : available.keySet()) {
				// Logger info is in db, apply settings.
				if (configured.containsKey(key)) {
					LoggerBean loggerBean = configured.get(key);
					Logger logger = available.get(key);
					setLevel(logger, loggerBean.getLevel());
					configured.remove(key);
				} else {
					// New logger, add to db.
					loggerBeanRepository.save(mapToLoggerBean(available
							.get(key)));
					added++;
				}
			}

			// Configured logger, not available on system anymore
			for (String key : configured.keySet()) {
				LoggerBean loggerBean = configured.get(key);
				loggerBeanRepository.delete(loggerBean);
				deleted++;
			}
			initDone = true;
			log.info("Total logger = {}, added {} and deleted {}",
					new Object[] { total, added, deleted });
			log.info("LoggerAdminService init done.");
		}
	}

	@Override
	public List<String> getLogLevels() {
		final String levels[] = { Level.ALL.toString(), Level.DEBUG.toString(),
				Level.INFO.toString(), Level.WARN.toString(),
				Level.ERROR.toString(), Level.FATAL.toString(),
				Level.OFF.toString(), Level.TRACE.toString() };
		return Arrays.asList(levels);
	}

	@Override
	public void reInit() {
		initDone = false;
		init();
	}

	@Override
	public void setLevelAndSave(String name, String level) {		
			Logger logger = LogManager.getLogger(name);
			if(Strings.isEmpty(level)) {
				// here it's like off, we save null so on init, it depends on logger configuration.
				logger.setLevel(Level.OFF);
			} else {
				logger.setLevel(Level.toLevel(level));
			}
			LoggerBean bean = loggerBeanRepository.findByName(name);
			// we save null as null into db
			bean.setLevel(level);
			loggerBeanRepository.save(bean);			
	}

	@Override
	public void setLevel(Logger logger, String level) {
		if (!Strings.isEmpty(level)) {
			logger.setLevel(Level.toLevel(level));
		}
	}
	
	private Map<String, Logger> getAllLoggerInstances() {
		Map<String, Logger> res = new HashMap<String, Logger>();
		@SuppressWarnings("unchecked")
		Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
		while (loggers.hasMoreElements()) {
			Logger logger = (Logger) loggers.nextElement();
			res.put(logger.getName(), logger);
		}
		return res;
	}

	public List<LoggerBean> getAllLogger() {
		List<LoggerBean> res = new ArrayList<LoggerBean>();

		for (@SuppressWarnings("unchecked")
		Enumeration<Logger> loggers = LogManager.getCurrentLoggers(); loggers
				.hasMoreElements();) {
			Logger logger = loggers.nextElement();
			res.add(mapToLoggerBean(logger));
		}
		return res;
	}

	private LoggerBean mapToLoggerBean(Logger src) {
		LoggerBean dest = new LoggerBean();
		dest.setName(src.getName());
		dest.setLevel(src.getLevel() == null ? null : src.getLevel().toString());
		return dest;
	}

}
