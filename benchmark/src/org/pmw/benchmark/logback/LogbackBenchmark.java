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

package org.pmw.benchmark.logback;

import java.io.File;

import org.pmw.benchmark.IBenchmark;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;

public class LogbackBenchmark implements IBenchmark {

	private Logger logger;
	private Appender<ILoggingEvent> appender;

	@Override
	public String getName() {
		return "logback";
	}

	@Override
	public void init(final File file) throws Exception {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.setLevel(Level.INFO);

		appender = createAppender(file, context);
		appender.start();

		logger.detachAndStopAllAppenders();
		logger.addAppender(appender);
	}

	@Override
	public void trace(final int index) {
		logger.trace("Trace: {}, PI: {}", index, Math.PI);
	}

	@Override
	public void debug(final int index) {
		logger.debug("Debug: {}, PI: {}", index, Math.PI);
	}

	@Override
	public void info(final int index) {
		logger.info("Info: {}, PI: {}", index, Math.PI);
	}

	@Override
	public void warning(final int index) {
		logger.warn("Warning: {}, PI: {}", index, Math.PI);
	}

	@Override
	public void error(final int index) {
		logger.error("Error: {}, PI: {}", index, Math.PI);
	}

	@Override
	public void dispose() throws Exception {
		appender.stop();
	}

	protected Appender<ILoggingEvent> createAppender(final File file, final LoggerContext context) {
		FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
		appender.setContext(context);
		appender.setAppend(false);
		appender.setFile(file.getAbsolutePath());

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);
		encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread] %class.%method\\(\\): %message%n");
		encoder.start();
		appender.setEncoder(encoder);
		return appender;
	}

}
