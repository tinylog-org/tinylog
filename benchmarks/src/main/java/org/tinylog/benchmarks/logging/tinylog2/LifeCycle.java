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

package org.tinylog.benchmarks.logging.tinylog2;

import java.nio.file.Path;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.tinylog.benchmarks.logging.AbstractLifeCycle;
import org.tinylog.benchmarks.logging.LocationInfo;
import org.tinylog.configuration.Configuration;
import org.tinylog.provider.ProviderRegistry;

/**
 * Life cycle for initializing and shutting down tinylog.
 */
@State(Scope.Benchmark)
public class LifeCycle extends AbstractLifeCycle {

	@Param
	private LocationInfo locationInfo;

	@Param({"false", "true"})
	private boolean async;

	/**
	 *
	 */
	public LifeCycle() {
	}

	@Override
	protected void init(final Path file) {
		Configuration.set("autoshutdown", "false");
		Configuration.set("level", "info");
		Configuration.set("writer", "file");
		Configuration.set("writer.buffered", Boolean.toString(async));
		Configuration.set("writer.file", file.toString());

		if (locationInfo == LocationInfo.NONE) {
			Configuration.set("writer.format", "{date:yyyy-MM-dd HH:mm:ss} [{thread}]: {message}");
		} else if (locationInfo == LocationInfo.CLASS_OR_CATEGORY_ONLY) {
			Configuration.set("writer.format", "{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}: {message}");
		} else {
			Configuration.set("writer.format", "{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}(): {message}");
		}

		Configuration.set("writingthread", Boolean.toString(async));
	}

	@Override
	protected void shutDown() throws InterruptedException {
		ProviderRegistry.getLoggingProvider().shutdown();
	}

}
