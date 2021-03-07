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

package org.tinylog.benchmarks.logging.log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
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

	/**
	 *
	 */
	public LifeCycle() {
	}

	@Override
	protected void init(final Path file) throws IOException {
		if (async) {
			System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		builder.append("<Configuration>");
		builder.append("<Appenders>");
		builder.append("<File name=\"file\" fileName=\"" + file + "\"");
		builder.append(" bufferedIO=\"" + async + "\" bufferSize=\"" + BUFFER_SIZE + "\">");
		builder.append("<PatternLayout><Pattern>");

		if (locationInfo == LocationInfo.NONE) {
			builder.append("%d{yyyy-MM-dd HH:mm:ss} [%t]: %m%n");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			builder.append("%d{yyyy-MM-dd HH:mm:ss} [%t] %c: %m%n");
		} else {
			builder.append("%d{yyyy-MM-dd HH:mm:ss} [%t] %C.%M(): %m%n");
		}

		builder.append("</Pattern></PatternLayout>");
		builder.append("</File>");
		builder.append("</Appenders>");
		builder.append("<Loggers>");
		builder.append("<Root level=\"info\" includeLocation=\"" + (locationInfo == LocationInfo.FULL) + "\">");
		builder.append("<AppenderRef ref=\"file\"/>");
		builder.append("</Root>");
		builder.append("</Loggers>");
		builder.append("</Configuration>");

		byte[] configuration = builder.toString().getBytes(StandardCharsets.UTF_8);
		ConfigurationSource source = new ConfigurationSource(new ByteArrayInputStream(configuration));
		Configurator.initialize(null, source);

		logger = LogManager.getLogger(Log4j2Benchmark.class);
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
		LogManager.shutdown();
	}

}
