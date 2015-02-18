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

package org.pmw.benchmark.frameworks;

import java.io.File;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * Test Logback.
 */
public final class Logback implements Framework {

	private static final String NAME = "Logback";
	private static final String NAME_ASYNC = NAME + " with async appender";

	private final boolean location;
	private final boolean async;
	private Logger logger;
	private Appender<ILoggingEvent> appender;

	public Logback(final boolean location, final boolean async) {
		this.location = location;
		this.async = async;
	}

	@Override
	public String getName() {
		return async ? NAME_ASYNC : NAME;
	}

	@Override
	public void init(final File file) {
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		logger = context.getLogger(Logger.ROOT_LOGGER_NAME);
		logger.setLevel(Level.INFO);

		appender = createAppender(file, context);
		appender.start();

		logger.detachAndStopAllAppenders();
		logger.addAppender(appender);
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
	public void dispose() {
		appender.stop();
		if (async) {
			((LoggerContext) LoggerFactory.getILoggerFactory()).stop();
		}
	}

	private Appender<ILoggingEvent> createAppender(final File file, final LoggerContext context) {
		if (async) {
			return createAsyncAppender(file, context);
		} else {
			return createFileAppender(file, context);
		}
	}

	private Appender<ILoggingEvent> createAsyncAppender(final File file, final LoggerContext context) {
		AsyncAppender appender = new AsyncAppender();
		appender.setIncludeCallerData(location);
		appender.setDiscardingThreshold(0);
		appender.setContext(context);

		Appender<ILoggingEvent> subAppender = createFileAppender(file, context);
		subAppender.start();
		appender.addAppender(subAppender);

		return appender;
	}

	private Appender<ILoggingEvent> createFileAppender(final File file, final LoggerContext context) {
		FileAppender<ILoggingEvent> appender = new FileAppender<>();
		appender.setContext(context);
		appender.setAppend(false);
		appender.setFile(file.getAbsolutePath());

		Encoder<ILoggingEvent> encoder = createEncoder(context);
		encoder.start();
		appender.setEncoder(encoder);
		return appender;
	}

	private LayoutWrappingEncoder<ILoggingEvent> createEncoder(final LoggerContext context) {
		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);
		if (location) {
			encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread] %class.%method\\(\\): %message%n");
		} else {
			encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss}: %message%n");
		}
		encoder.setImmediateFlush(!async);
		return encoder;
	}

}
