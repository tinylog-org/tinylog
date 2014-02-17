/*
 * Copyright 2014 Martin Winandy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.pmw.benchmark.log4j2;

import java.io.File;
import java.io.Serializable;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.pmw.benchmark.ILoggingFramework;

public class Log4j2 implements ILoggingFramework {

	private Logger logger;
	private Appender appender;

	@Override
	public String getName() {
		return "log4j 2";
	}

	@Override
	public void init(final File file) throws Exception {
		logger = (Logger) LogManager.getLogger();
		Configuration configuration = logger.getContext().getConfiguration();

		for (Appender appender : configuration.getAppenders().values()) {
			logger.removeAppender(appender);
		}
		appender = createAppender(file, configuration);
		appender.start();
		logger.addAppender(appender);

		logger.setLevel(Level.INFO);
	}

	@Override
	public void trace(final Object obj) {
		logger.trace("Trace: {}", obj);
	}

	@Override
	public void debug(final Object obj) {
		logger.debug("Debug: {}", obj);
	}

	@Override
	public void info(final Object obj) {
		logger.info("Info: {}", obj);
	}

	@Override
	public void warning(final Object obj) {
		logger.warn("Warning: {}", obj);
	}

	@Override
	public void error(final Object obj) {
		logger.error("Error: {}", obj);
	}

	@Override
	public void dispose() throws Exception {
		appender.stop();
	}

	protected Layout<? extends Serializable> createLayout(final Configuration configuration) {
		return PatternLayout.createLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n", configuration, null, null, null, null);
	}

	protected Appender createAppender(final File file, final Configuration configuration) {
		return FileAppender.createAppender(file.getAbsolutePath(), "false", null, "File", null, null, "false", null, createLayout(configuration), null, null,
				null, configuration);
	}

}
