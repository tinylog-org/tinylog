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
import java.nio.file.Path;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.writers.FileWriter;
import org.tinylog.benchmarks.logging.AbstractLifeCycle;
import org.tinylog.benchmarks.logging.LocationInfo;

/**
 * Life cycle for initializing and shutting down tinylog.
 */
@State(Scope.Benchmark)
public class LifeCycle extends AbstractLifeCycle {

	@Param({"false", "true"})
	private boolean async;

	private FileWriter writer;

	/**
	 *
	 */
	public LifeCycle() {
	}

	@Override
	protected void init(final Path file) {
		writer = new FileWriter(file.toString(), async);

		Configurator configurator = Configurator.defaultConfig();
		configurator.level(org.pmw.tinylog.Level.INFO);
		configurator.writer(writer);

		if (getLocationInfo() == LocationInfo.NONE) {
			configurator
				.formatPattern("{date:yyyy-MM-dd HH:mm:ss} - {thread} - {level}: {message}");
		} else if (getLocationInfo() == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			configurator
				.formatPattern("{date:yyyy-MM-dd HH:mm:ss} - {thread} - {class} - {level}: {message}");
		} else {
			configurator
				.formatPattern("{date:yyyy-MM-dd HH:mm:ss} - {thread} - {class}.{method}() - {level}: {message}");
		}

		if (async) {
			configurator = configurator.writingThread(null);
		}

		configurator.activate();
	}

	@Override
	protected void shutDown() throws IOException {
		if (async) {
			Configurator.shutdownWritingThread(true);
		}

		writer.close();
	}

}
