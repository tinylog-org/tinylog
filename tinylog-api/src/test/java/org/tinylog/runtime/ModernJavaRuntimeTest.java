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

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

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
	 * Verifies that correct timestamps will be created.
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting between creation of both timestamps
	 */
	@Test
	public void creatingTimestamp() throws InterruptedException {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		Timestamp timestamp = runtime.createTimestamp();
		assertThat(timestamp).isInstanceOf(FastTimestamp.class);
		assertThat(timestamp.toInstant()).isBetween(Instant.now().minusSeconds(1), Instant.now());

		Thread.sleep(2);

		assertThat(runtime.createTimestamp().toInstant()).isAfter(timestamp.toInstant());
	}

	/**
	 * Verifies that a correct timestamp formatter will be created.
	 */
	@Test
	public void creatingTimestampFormatter() {
		ModernJavaRuntime runtime = new ModernJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd hh:mm", Locale.US);
		assertThat(formatter).isInstanceOf(FastTimestampFormatter.class);

		Timestamp timestamp = mock(Timestamp.class);
		when(timestamp.toDate()).thenReturn(Date.from(LocalDateTime.of(1985, 06, 03, 12, 30).atZone(ZoneId.systemDefault()).toInstant()));

		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30");
	}

}
