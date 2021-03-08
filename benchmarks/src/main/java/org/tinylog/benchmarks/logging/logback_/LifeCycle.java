/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.benchmarks.logging.logback_;

import java.nio.file.Path;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.slf4j.LoggerFactory;
import org.tinylog.benchmarks.logging.AbstractLifeCycle;
import org.tinylog.benchmarks.logging.LocationInfo;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.FileSize;

/**
 * Life cycle for initializing and shutting down Logback.
 */
@State(Scope.Benchmark)
public class LifeCycle extends AbstractLifeCycle {

	private static final int BUFFER_SIZE = 64 * 1024;

	@Param
	private LocationInfo locationInfo;

	@Param({"false", "true"})
	private boolean async;

	private LoggerContext context;
	private Logger logger;
	private Appender<ILoggingEvent> appender;

	/**
	 *
	 */
	public LifeCycle() {
	}

	@Override
	protected void init(final Path file) {
		context = (LoggerContext) LoggerFactory.getILoggerFactory();

		logger = context.getLogger(Logback_Benchmark.class);
		logger.setLevel(ch.qos.logback.classic.Level.INFO);

		appender = async ? createAsyncAppender(file.toString()) : createFileAppender(file.toString());
		appender.start();

		context.getLogger(Logger.ROOT_LOGGER_NAME).detachAndStopAllAppenders();
		context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(appender);
	}

	/**
	 * Gets the current logger.
	 *
	 * @return Current logger
	 */
	public Logger getLogger() {
		return logger;
	}

	@Override
	protected void shutDown() {
		appender.stop();
		context.stop();
	}

	/**
	 * Creates an asynchronous appender that contains a file appender for the given file.
	 *
	 * @param file Path to log file
	 * @return Created asynchronous appender
	 */
	private Appender<ILoggingEvent> createAsyncAppender(final String file) {
		AsyncAppender appender = new AsyncAppender();
		appender.setIncludeCallerData(locationInfo == LocationInfo.FULL);
		appender.setDiscardingThreshold(0);
		appender.setContext(context);

		Appender<ILoggingEvent> subAppender = createFileAppender(file);
		subAppender.start();
		appender.addAppender(subAppender);

		return appender;
	}

	/**
	 * Creates a file appender for the given file.
	 *
	 * @param file Path to log file
	 * @return Created file appender
	 */
	private Appender<ILoggingEvent> createFileAppender(final String file) {
		FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>();
		appender.setContext(context);
		appender.setAppend(false);
		appender.setBufferSize(new FileSize(BUFFER_SIZE));
		appender.setImmediateFlush(!async);
		appender.setFile(file);

		PatternLayoutEncoder encoder = new PatternLayoutEncoder();
		encoder.setContext(context);

		if (locationInfo == LocationInfo.NONE) {
			encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread]: %message%n");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread] %logger: %message%n");
		} else {
			encoder.setPattern("%date{yyyy-MM-dd HH:mm:ss} [%thread] %class.%method\\(\\): %message%n");
		}

		encoder.setImmediateFlush(!async);
		encoder.start();
		appender.setEncoder(encoder);

		return appender;
	}

}
