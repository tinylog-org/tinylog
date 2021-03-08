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

package org.tinylog.benchmarks.logging.log4j1__;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.AbstractLifeCycle;
import org.tinylog.benchmarks.logging.LocationInfo;

/**
 * Life cycle for initializing and shutting down Log4j.
 */
@State(Scope.Benchmark)
public class LifeCycle extends AbstractLifeCycle {

	private static final int BUFFER_SIZE = 64 * 1024;

	@Param
	private LocationInfo locationInfo;

	@Param({"false", "true"})
	private boolean async;

	private Logger logger;
	private Path file;
	private Appender appender;

	/**
	 *
	 */
	public LifeCycle() {
	}

	@Override
	protected void init(final Path file) throws IOException {
		appender = createAppender(file.toString());

		logger = Logger.getLogger(Log4j1__Benchmark.class);
		logger.removeAllAppenders();
		logger.addAppender(appender);
		logger.setLevel(org.apache.log4j.Level.INFO);
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
		appender.close();
	}

	/**
	 * Creates a file appender.
	 *
	 * @param file Path to log file
	 * @return Created appender
	 * @throws IOException Failed creating appender
	 */
	private Appender createAppender(final String file) throws IOException {
		Layout layout;
		if (locationInfo == LocationInfo.NONE) {
			layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t]: %m%n");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %c: %m%n");
		} else {
			layout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n");
		}

		if (async) {
			AsyncAppender appender = new AsyncAppender();
			appender.setLocationInfo(locationInfo == LocationInfo.FULL);
			appender.addAppender(new FileAppender(layout, file, false, true, BUFFER_SIZE));
			return appender;
		} else {
			return new FileAppender(layout, file, false, false, 0);
		}
	}

}
