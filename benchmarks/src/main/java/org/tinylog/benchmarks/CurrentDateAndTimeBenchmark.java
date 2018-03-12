/*
 * Copyright 2018 Martin Winandy
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

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * Benchmark for comparing the legacy and modern date and time API for getting the current date and time and formatting
 * it.
 */
public class CurrentDateAndTimeBenchmark {

	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

	private static final Instant INSTANT = Instant.now();
	private static final Date DATE = Date.from(INSTANT);

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(ZoneId.systemDefault());
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

	/** */
	public CurrentDateAndTimeBenchmark() {
	}

	/**
	 * Gets the current date and time by using a legacy {@link Date}.
	 *
	 * @return Legacy date with current date and time
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public Date currentDate() {
		return new Date();
	}

	/**
	 * Gets the current date and time by using a modern {@link Instant}.
	 *
	 * @return Modern instant with current date and time
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public Instant currentInstant() {
		return Instant.now();
	}

	/**
	 * Formats a legacy {@link Date} by using {@link SimpleDateFormat}.
	 *
	 * @return Formatted date
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public String formatDate() {
		synchronized (SIMPLE_DATE_FORMAT) {
			return SIMPLE_DATE_FORMAT.format(DATE);
		}
	}

	/**
	 * Formats a modern {@link Instant} by using {@link DateTimeFormatter}.
	 *
	 * @return Formatted instant
	 */
	@Benchmark
	@BenchmarkMode(Mode.Throughput)
	public String formatInstant() {
		return DATE_TIME_FORMATTER.format(INSTANT);
	}

}
