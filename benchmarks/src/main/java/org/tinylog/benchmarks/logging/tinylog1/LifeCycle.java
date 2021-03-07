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

package org.tinylog.benchmarks.logging.tinylog1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.writers.FileWriter;

/**
 * Life cycle for initializing and shutting down tinylog.
 */
@State(Scope.Benchmark)
public class LifeCycle {

	@Param({"false", "true"})
	private boolean async;

	private Path file;
	private FileWriter writer;

	/**
	 *
	 */
	public LifeCycle() {
	}

	/**
	 * Initializes tinylog.
	 *
	 * @throws IOException Failed creating temporary log file
	 */
	@Setup(Level.Trial)
	public void init() throws IOException {
		file = Files.createTempFile("tinylog1_", ".log");
		writer = new FileWriter(file.toString(), async);

		Configurator configurator = Configurator.defaultConfig()
				.level(org.pmw.tinylog.Level.INFO)
				.formatPattern("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}(): {message}")
				.writer(writer);

		if (async) {
			configurator = configurator.writingThread(null);
		}

		configurator.activate();
	}

	/**
	 * Shuts down tinylog.
	 *
	 * @throws IOException Failed to delete log file
	 */
	@TearDown(Level.Trial)
	public void release() throws IOException {
		if (async) {
			Configurator.shutdownWritingThread(true);
		}

		writer.close();
		Files.delete(file);
	}

}
