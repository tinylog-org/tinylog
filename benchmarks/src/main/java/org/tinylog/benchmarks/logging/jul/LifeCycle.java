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

package org.tinylog.benchmarks.logging.jul;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.tinylog.benchmarks.logging.LocationInfo;

/**
 * Life cycle for initializing and shutting down java.util.logging.
 */
@State(Scope.Benchmark)
public class LifeCycle {

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

	/**
	 * Initializes java.util.logging.
	 *
	 * @throws IOException Failed creating temporary log file
	 */
	@Setup(Level.Trial)
	public void init() throws IOException {
		logger = Logger.getLogger(JulBenchmark.class.getName());
		file = Files.createTempFile("jul_", ".log");
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

	/**
	 * Shuts down java.util.logging.
	 *
	 * @throws IOException Failed to delete log file
	 */
	@TearDown(Level.Trial)
	public void release() throws IOException {
		handler.close();
		logger.removeHandler(handler);
		Files.delete(file);
	}

}
