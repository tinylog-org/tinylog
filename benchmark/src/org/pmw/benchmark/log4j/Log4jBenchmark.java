/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.benchmark.log4j;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.pmw.benchmark.IBenchmark;

public class Log4jBenchmark implements IBenchmark {

	private Logger logger;
	private Appender appender;

	@Override
	public String getName() {
		return "log4j";
	}

	@Override
	public void init(final File file) throws Exception {
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		appender = createAppender(file);
		logger.addAppender(appender);
		logger.setLevel(Level.INFO);
	}

	@Override
	public void trace(final Object obj) {
		logger.trace(MessageFormat.format("Trace: {0}", obj));
	}

	@Override
	public void debug(final Object obj) {
		logger.debug(MessageFormat.format("Debug: {0}", obj));
	}

	@Override
	public void info(final Object obj) {
		logger.info(MessageFormat.format("Info: {0}", obj));
	}

	@Override
	public void warning(final Object obj) {
		logger.warn(MessageFormat.format("Warning: {0}", obj));
	}

	@Override
	public void error(final Object obj) {
		logger.error(MessageFormat.format("Error: {0}", obj));
	}

	@Override
	public void dispose() throws Exception {
		appender.close();
	}

	protected Appender createAppender(final File file) throws IOException {
		return new FileAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n"), file.getAbsolutePath(), false);
	}

}
