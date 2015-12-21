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

package org.tinylog.benchmark.frameworks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * Test Apache Log4j 2.
 */
public final class Log4j2 implements Framework {

	private static final String NAME = "Log4j 2";
	private static final String NAME_ASYNC = NAME + " with async logger";

	private final boolean location;
	private final boolean async;
	private Logger logger;
	private Appender appender;

	public Log4j2(final boolean location, final boolean async) {
		this.location = location;
		this.async = async;
	}

	@Override
	public String getName() {
		return async ? NAME_ASYNC : NAME;
	}

	@Override
	public void init(final File file) throws IOException {
		if (async) {
			System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<Configuration>");
		builder.append("<Appenders>");
		builder.append("<File name=\"file\" fileName=\"" + file.getAbsolutePath() + "\" bufferedIO=\"" + async + "\">");
		if (location) {
			builder.append("<PatternLayout><Pattern>%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n</Pattern></PatternLayout>");
		} else {
			builder.append("<PatternLayout><Pattern>%d{yyyy-MM-dd HH:mm:ss}: %m%n</Pattern></PatternLayout>");
		}
		builder.append("</File>");
		builder.append("</Appenders>");
		builder.append("<Loggers>");
		builder.append("<Root level=\"info\" includeLocation=\"" + location + "\">");
		builder.append("<AppenderRef ref=\"file\"/>");
		builder.append("</Root>");
		builder.append("</Loggers>");
		builder.append("</Configuration>");

		ConfigurationSource source = new ConfigurationSource(new ByteArrayInputStream(builder.toString().getBytes(Charset.forName("UTF-8"))));
		Configurator.initialize(null, source);

		logger = (Logger) LogManager.getLogger();
		appender = logger.getContext().getConfiguration().getAppenders().get("file");
	}

	@Override
	public void write(final long number) {
		logger.trace("Trace: {}", number);
		logger.debug("Debug: {}", number);
		logger.info("Info: {}", number);
		logger.warn("Warning: {}", number);
		logger.error("Error: {}", number);
	}

	@Override
	public boolean calculate(final List<Long> primes, final long number) {
		for (Long prime : primes) {
			if (number % prime == 0L) {
				logger.trace("{} is not prime", number);
				return false;
			}
		}
		logger.info("{} is prime", number);
		return true;
	}

	@Override
	public void dispose() throws InterruptedException {
		if (async) {
			AsyncLogger.stop();
		}

		appender.stop();
	}

}
