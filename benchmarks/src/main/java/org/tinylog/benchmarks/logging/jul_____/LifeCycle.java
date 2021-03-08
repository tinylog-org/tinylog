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

package org.tinylog.benchmarks.logging.jul_____;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.AbstractLifeCycle;
import org.tinylog.benchmarks.logging.LocationInfo;

/**
 * Life cycle for initializing and shutting down java.util.logging.
 */
@State(Scope.Benchmark)
public class LifeCycle extends AbstractLifeCycle {

	@Param
	private LocationInfo locationInfo;

	private Logger logger;
	private Path file;
	private FileHandler handler;

	/**
	 *
	 */
	public LifeCycle() {
	}

	@Override
	protected void init(final Path file) throws IOException {
		logger = Logger.getLogger(Jul_____Benchmark.class.getName());
		handler = new FileHandler(file.toString(), false);
		handler.setFormatter(new SimpleFormatter(locationInfo));
		logger.addHandler(handler);
		logger.setUseParentHandlers(false);
		logger.setLevel(java.util.logging.Level.INFO);
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
		handler.close();
		logger.removeHandler(handler);
	}

}
