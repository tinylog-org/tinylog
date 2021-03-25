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

package org.tinylog.benchmarks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.profile.ExternalProfiler;
import org.openjdk.jmh.results.BenchmarkResult;
import org.openjdk.jmh.results.Result;

/**
 * JDK Flight Recorder as external JMH profiler.
 */
public final class JdkFlightRecorderProfiler implements ExternalProfiler {

	private static final String OUTPUT_DIRECTORY_KEY = "jmh.profiler.directory";

	/** */
	public JdkFlightRecorderProfiler() {
	}

	@Override
	public String getDescription() {
		return "JDK Flight Recorder profiler";
	}

	@Override
	public Collection<String> addJVMOptions(final BenchmarkParams params) {
		String workingDirectory = new File("").getAbsolutePath();
		String directory = System.getenv().getOrDefault(OUTPUT_DIRECTORY_KEY, workingDirectory);
		String benchmark = params.id();

		Path folder = Paths.get(directory);
		Path file = folder.resolve(benchmark + ".jfr");

		try {
			Files.createDirectories(folder);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}

		return Arrays.asList(
			"-XX:+UnlockCommercialFeatures",
			"-XX:+FlightRecorder",
			"-XX:StartFlightRecording=filename=" + file.toAbsolutePath()
		);
	}

	@Override
	public Collection<String> addJVMInvokeOptions(final BenchmarkParams params) {
		return Collections.emptyList();
	}

	@Override
	public boolean allowPrintOut() {
		return true;
	}

	@Override
	public boolean allowPrintErr() {
		return true;
	}

	@Override
	public void beforeTrial(final BenchmarkParams params) {
		// Nothing to do
	}

	@Override
	public Collection<? extends Result<?>> afterTrial(final BenchmarkResult result, final long pid, final File stdOut,
			final File stdErr) {
		return Collections.emptyList();
	}

}
