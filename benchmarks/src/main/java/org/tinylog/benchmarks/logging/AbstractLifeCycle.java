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

package org.tinylog.benchmarks.logging;

import java.nio.file.Files;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Base life cycle for initializing and shutting logging frameworks down.
 */
@State(Scope.Benchmark)
public abstract class AbstractLifeCycle {

	@Param
	private LocationInfo locationInfo;

	private Path file;

	/**
	 *
	 */
	protected AbstractLifeCycle() {
	}

	/**
	 * Creates a temporary log file and initializes the logging framework.
	 *
	 * @throws Exception Failed to create a temporary log file or to initialize the logging framework
	 */
	@Setup(Level.Trial)
	public final void init() throws Exception {
		file = Files.createTempFile("jmh_logging_", ".log");
		init(file);
	}

	/**
	 * Shuts the logging framework down and deletes the temporary log file.
	 *
	 * @throws Exception Failed to shut the logging framework down to delete the log file
	 */
	@TearDown(Level.Trial)
	public final void release() throws Exception {
		shutDown();
		Files.delete(file);
	}

	/**
	 * Initializes the logging framework.
	 *
	 * @param file Log file
	 * @throws Exception Failed to initialize the logging framework
	 */
	protected abstract void init(Path file) throws Exception;

	/**
	 * Shuts the logging framework down.
	 *
	 * @throws Exception Failed to shut the logging framework down
	 */
	protected abstract void shutDown() throws Exception;

	/**
	 * Gets the configured location information.
	 *
	 * @return Configured location information
	 */
	protected final LocationInfo getLocationInfo() {
		return locationInfo;
	}

}
