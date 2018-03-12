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

package org.tinylog.runtime;

import java.time.Instant;
import java.util.Locale;

import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.util.SimpleTimestamp;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ModernJavaRuntime}.
 */
public final class ModernJavaRuntimeTest {

	/**
	 * Verifies that the console writer will be returned as default writer.
	 */
	@Test
	public void defaultWriter() {
		assertThat(new ModernJavaRuntime().getDefaultWriter()).isEqualTo("console");
	}

	/**
	 * Verifies that the process ID will be returned.
	 */
	@Test
	public void processId() {
		assertThat(new ModernJavaRuntime().getProcessId()).isEqualTo(ProcessHandle.current().pid());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly.
	 */
	@Test
	public void callerClassName() {
		assertThat(new ModernJavaRuntime().getCallerClassName(1)).isEqualTo(ModernJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller can be returned, if {@link sun.reflect.Reflection} is
	 * not available.
	 */
	@Test
	public void missingSunReflection() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();
		Whitebox.setInternalState(runtime, boolean.class, false);
		assertThat(runtime.getCallerClassName(1)).isEqualTo(ModernJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly.
	 */
	@Test
	public void callerStackTraceElement() {
		assertThat(new ModernJavaRuntime().getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that correct timestamps with millisecond precision will be created.
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting between creation of both timestamps
	 */
	@Test
	public void creatingMillisecondPreciseTimestamp() throws InterruptedException {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		Timestamp timestamp = runtime.createTimestamp(true);
		assertThat(timestamp).isInstanceOf(FastTimestamp.class);
		assertThat(timestamp.toInstant()).isBetween(Instant.now().minusSeconds(1), Instant.now());

		Thread.sleep(2);

		assertThat(runtime.createTimestamp(true).toInstant()).isAfter(timestamp.toInstant());
	}

	/**
	 * Verifies that correct timestamps with nanosecond precision will be created.
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting between creation of both timestamps
	 */
	@Test
	public void creatingNanosecondPreciseTimestamp() throws InterruptedException {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		Timestamp timestamp = runtime.createTimestamp(false);
		assertThat(timestamp).isInstanceOf(PreciseTimestamp.class);
		assertThat(timestamp.toInstant()).isBetween(Instant.now().minusSeconds(1), Instant.now());

		Thread.sleep(2);

		assertThat(runtime.createTimestamp(false).toInstant()).isAfter(timestamp.toInstant());
	}

	/**
	 * Verifies that an timestamp formatter with millisecond precision will be created for "yyyy-MM-dd hh:mm:ss.SSS",.
	 */
	@Test
	public void creatingTimestampFormatterForTimeWithMilliseconds() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd hh:mm:ss.SSS", Locale.US);
		assertThat(formatter.requiresNanoseconds()).isFalse();

		Timestamp timestamp = new SimpleTimestamp(1985, 6, 3, 12, 30, 55, 999_001_002);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30:55.999");
	}

	/**
	 * Verifies that an timestamp formatter with nanosecond precision will be created for "yyyy-MM-dd hh:mm:ss.SSSSSS".
	 */
	@Test
	public void creatingTimestampFormatterForTimeWithMicroseconds() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd hh:mm:ss.SSSSSS", Locale.US);
		assertThat(formatter.requiresNanoseconds()).isTrue();

		Timestamp timestamp = new SimpleTimestamp(1985, 6, 3, 12, 30, 55, 999_001_002);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30:55.999001");
	}

	/**
	 * Verifies that an timestamp formatter with nanosecond precision will be created for "yyyy-MM-dd hh:mm:ss.n".
	 */
	@Test
	public void creatingTimestampFormatterForTimeWithNanoseconds() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd hh:mm:ss.n", Locale.US);
		assertThat(formatter.requiresNanoseconds()).isTrue();

		Timestamp timestamp = new SimpleTimestamp(1985, 6, 3, 12, 30, 55, 999_001_002);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30:55.999001002");
	}

	/**
	 * Verifies that an timestamp formatter with nanosecond precision will be created for "yyyy-MM-dd N".
	 */
	@Test
	public void creatingTimestampFormatterForNanosecondsOfDay() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd N", Locale.US);
		assertThat(formatter.requiresNanoseconds()).isTrue();

		Timestamp timestamp = new SimpleTimestamp(1985, 6, 3, 12, 30, 55, 999_001_002);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 45055999001002");
	}

}
