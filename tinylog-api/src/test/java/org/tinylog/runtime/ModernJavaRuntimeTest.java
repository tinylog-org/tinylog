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
import org.tinylog.Logger;
import org.tinylog.util.TimestampFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link ModernJavaRuntime}.
 */
public final class ModernJavaRuntimeTest {

	/**
	 * Verifies that this runtime is not Android.
	 */
	@Test
	public void notAndroid() {
		assertThat(new ModernJavaRuntime().isAndroid()).isFalse();
	}

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
	 * Verifies that a valid start time will be returned.
	 *
	 * @throws InterruptedException
	 *             Interrupted while sleeping
	 */
	@Test
	public void startTime() throws InterruptedException {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		Timestamp first = runtime.getStartTime();
		assertThat(first.toInstant()).isBefore(Instant.now());

		Thread.sleep(1);

		Timestamp second = runtime.getStartTime();
		assertThat(second.toInstant()).isEqualTo(first.toInstant());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly, if depth in stack trace is
	 * defined as index.
	 */
	@Test
	public void callerClassNameByIndex() {
		assertThat(new ModernJavaRuntime().getCallerClassName(1)).isEqualTo(ModernJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly, if successor in stack trace
	 * is defined.
	 */
	@Test
	public void callerClassNameBySuccessor() {
		assertThat(new ModernJavaRuntime().getCallerClassName(ModernJavaRuntime.class.getName()))
			.isEqualTo(ModernJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly, if depth in stack trace is
	 * defined as index.
	 */
	@Test
	public void callerStackTraceElementByIndex() {
		assertThat(new ModernJavaRuntime().getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly, if successor in stack
	 * trace is defined.
	 */
	@Test
	public void callerStackTraceElementBySuccessor() {
		String className = ModernJavaRuntime.class.getName();
		assertThat(new ModernJavaRuntime().getCallerStackTraceElement(className)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that an exception will be thrown, if stack trace does not contain the expected successor.
	 */
	@Test
	public void missingSuccessorForCallerStackTraceElement() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		assertThatThrownBy(() -> runtime.getCallerStackTraceElement(Logger.class.getName()))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining(Logger.class.getName());
	}

	/**
	 * Verifies that timestamps with nanosecond precision will be created.
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting between creation of both timestamps
	 */
	@Test
	public void createTimestamp() throws InterruptedException {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		Timestamp timestamp = runtime.createTimestamp();
		assertThat(timestamp).isInstanceOf(PreciseTimestamp.class);
		assertThat(timestamp.toInstant()).isBetween(Instant.now().minusSeconds(1), Instant.now());

		Thread.sleep(2);

		assertThat(runtime.createTimestamp().toInstant()).isAfter(timestamp.toInstant());
	}

	/**
	 * Verifies that a precise timestamp formatter will be created.
	 */
	@Test
	public void createTimestampFormatter() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
		assertThat(formatter).isInstanceOf(PreciseTimestampFormatter.class);

		Timestamp timestamp = TimestampFactory.create(1985, 6, 3, 12, 30, 55, 999_001_002);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30:55.999");
	}

}
