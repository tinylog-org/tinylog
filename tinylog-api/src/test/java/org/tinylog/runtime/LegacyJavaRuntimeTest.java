/*
 * Copyright 2016 Martin Winandy
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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Locale;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.tinylog.Logger;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.TimestampFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link LegacyJavaRuntime}.
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public final class LegacyJavaRuntimeTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

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
		assertThat(new LegacyJavaRuntime().getDefaultWriter()).isEqualTo("console");
	}

	/**
	 * Verifies that the process ID will be returned.
	 */
	@Test
	public void processId() {
		assertThat(new LegacyJavaRuntime().getProcessId()).isEqualTo(ProcessHandle.current().pid());
	}

	/**
	 * Verifies that a valid start time will be returned.
	 *
	 * @throws InterruptedException
	 *             Interrupted while sleeping
	 */
	@Test
	public void startTime() throws InterruptedException {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();

		Timestamp first = runtime.getStartTime();
		assertThat(first.toInstant()).isBefore(Instant.now());

		Thread.sleep(1);

		Timestamp second = runtime.getStartTime();
		assertThat(second.toInstant()).isEqualTo(first.toInstant());
	}

	/**
	 * Verifies that there is a fallback, if {@link RuntimeMXBean#getName()} doesn't contain a numeric process ID (pid).
	 */
	@Test
	@PrepareForTest(LegacyJavaRuntime.class)
	public void invalidProcessId() {
		RuntimeMXBean bean = mock(RuntimeMXBean.class);
		when(bean.getName()).thenReturn("abc@test");

		mockStatic(ManagementFactory.class);
		when(ManagementFactory.getRuntimeMXBean()).thenReturn(bean);

		assertThat(new LegacyJavaRuntime().getProcessId()).isEqualTo(-1);
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("abc");
	}

	/**
	 * Verifies that there is a fallback, if {@link RuntimeMXBean#getName()} doesn't contain the expected '@' character.
	 */
	@Test
	@PrepareForTest(LegacyJavaRuntime.class)
	public void invalidJvmName() {
		RuntimeMXBean bean = mock(RuntimeMXBean.class);
		when(bean.getName()).thenReturn("test");

		mockStatic(ManagementFactory.class);
		when(ManagementFactory.getRuntimeMXBean()).thenReturn(bean);

		assertThat(new LegacyJavaRuntime().getProcessId()).isEqualTo(-1);
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("test");
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly, if depth in stack trace is
	 * defined as index.
	 */
	@Test
	public void callerClassNameByIndex() {
		assertThat(new LegacyJavaRuntime().getCallerClassName(1)).isEqualTo(LegacyJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly, if successor in stack
	 * trace is defined.
	 */
	@Test
	public void callerClassNameBySuccessor() {
		assertThat(new LegacyJavaRuntime().getCallerClassName(LegacyJavaRuntime.class.getName()))
			.isEqualTo(LegacyJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller can be returned, if {@link sun.reflect.Reflection} is
	 * not available.
	 */
	@Test
	public void missingSunReflection() {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();
		Whitebox.setInternalState(runtime, boolean.class, false);
		assertThat(runtime.getCallerClassName(1)).isEqualTo(LegacyJavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly, if depth in stack trace is
	 * defined as index.
	 */
	@Test
	public void callerStackTraceElementByIndex() {
		assertThat(new LegacyJavaRuntime().getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly, if successor in stack
	 * trace is defined.
	 */
	@Test
	public void callerStackTraceElementBySuccessor() {
		String className = LegacyJavaRuntime.class.getName();
		assertThat(new LegacyJavaRuntime().getCallerStackTraceElement(className)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that an exception will be thrown, if stack trace does not contain the expected successor.
	 */
	@Test
	public void missingSuccessorForCallerStackTraceElement() {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();

		assertThatThrownBy(() -> runtime.getCallerStackTraceElement(Logger.class.getName()))
			.isInstanceOf(IllegalStateException.class)
			.hasMessageContaining(Logger.class.getName());
	}

	/**
	 * Verifies that the complete stack trace element of a caller can be returned, if
	 * {@link Throwable#getStackTraceElement(int)} is not available.
	 */
	@Test
	public void missingStackTraceElementGetter() {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();
		Whitebox.setInternalState(runtime, Method.class, (Object) null);
		assertThat(runtime.getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that the complete stack trace element of a caller can be returned, if
	 * {@link Throwable#getStackTraceElement(int)} is not accessible.
	 */
	@Test
	public void notAccessibleSingleStackTraceElementGetter() {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();

		Method method = Whitebox.getMethod(LegacyJavaRuntimeTest.class, "getStackTraceElement", int.class);
		method.setAccessible(false);
		Whitebox.setInternalState(runtime, Method.class, method);

		assertThat(runtime.getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce(IllegalAccessException.class.getName());
	}

	/**
	 * Verifies that the complete stack trace element of a caller can be returned, if
	 * {@link Throwable#getStackTraceElement(int)} throws an exception.
	 */
	@Test
	public void stackTraceElementGetterThrowsException() {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();

		Method method = Whitebox.getMethod(LegacyJavaRuntimeTest.class, "getStackTraceElement", int.class);
		Whitebox.setInternalState(runtime, Method.class, method);

		assertThat(runtime.getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce(UnsupportedOperationException.class.getName());
	}

	/**
	 * Verifies that timestamps with millisecond precision will be created.
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting between creation of both timestamps
	 */
	@Test
	public void createTimestamp() throws InterruptedException {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();

		Timestamp timestamp = runtime.createTimestamp();
		assertThat(timestamp).isInstanceOf(LegacyTimestamp.class);
		assertThat(timestamp.toInstant()).isBetween(Instant.now().minusSeconds(1), Instant.now());

		Thread.sleep(2);

		assertThat(runtime.createTimestamp().toInstant()).isAfter(timestamp.toInstant());
	}

	/**
	 * Verifies that a legacy timestamp formatter will be created.
	 */
	@Test
	public void createTimestampFormatter() {
		LegacyJavaRuntime runtime = new LegacyJavaRuntime();

		TimestampFormatter formatter = runtime.createTimestampFormatter("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
		assertThat(formatter).isInstanceOf(LegacyTimestampFormatter.class);

		Timestamp timestamp = TimestampFactory.create(1985, 6, 3, 12, 30, 55, 999_001_002);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30:55.999");
	}

	/**
	 * Used in {@link #stackTraceElementGetterThrowsException()} as replacement for
	 * {@link Throwable#getStackTraceElement(int)} to test exception handling.
	 *
	 * @param index
	 *            Position of stack trace element
	 * @return Nothing, will always throw an exception
	 *
	 * @throws UnsupportedOperationException
	 *            Always thrown on each call
	 */
	@SuppressWarnings("unused")
	private static StackTraceElement getStackTraceElement(final int index) {
		throw new UnsupportedOperationException();
	}

}
