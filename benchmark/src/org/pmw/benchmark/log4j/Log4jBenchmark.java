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
import java.text.MessageFormat;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.pmw.benchmark.IBenchmark;

public class Log4jBenchmark implements IBenchmark {

	private Logger logger;
	private FileAppender appender;

	@Override
	public String getName() {
		return "log4j";
	}

	@Override
	public void log(final int index) {
		logger.trace(MessageFormat.format("Trace: {0}, PI: {1}", index, Math.PI));
		logger.debug(MessageFormat.format("Debug: {0}, PI: {1}", index, Math.PI));
		logger.info(MessageFormat.format("Info: {0}, PI: {1}", index, Math.PI));
		logger.warn(MessageFormat.format("Warning: {0}, PI: {1}", index, Math.PI));
		logger.error(MessageFormat.format("Error: {0}, PI: {1}", index, Math.PI));
	}

	@Override
	public void init(final File file) throws Exception {
		logger = Logger.getRootLogger();
		logger.removeAllAppenders();
		appender = new FileAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n"), file.getAbsolutePath(), false);
		logger.addAppender(appender);
		logger.setLevel(Level.INFO);
	}

	@Override
	public void dispose() throws Exception {
		appender.close();
	}

}
