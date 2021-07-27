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

import java.time.Instant;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.Logger;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.TimestampFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link RuntimeProvider}.
 */
public final class RuntimeProviderTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	private String runtime;
	private String version;

	/**
	 * Stores backup of runtime name system property.
	 */
	@Before
	public void init() {
		runtime = System.getProperty("java.runtime.name");
		version = System.getProperty("java.version");
	}

	/**
	 * Resets runtime name system property and runtime dialect.
	 *
	 * @throws Exception
	 *             Failed resetting runtime dialect
	 */
	@After
	public void reset() throws Exception {
		System.setProperty("java.runtime.name", runtime);
		System.setProperty("java.version", version);

		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		Whitebox.setInternalState(RuntimeProvider.class, RuntimeDialect.class, dialect);
	}

	/**
	 * Verifies that both context class loaders will be returned, if available.
	 */
	@Test
	public void classLoaderFromThread() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			ClassLoader mock = mock(ClassLoader.class);
			Thread.currentThread().setContextClassLoader(mock);
			assertThat(RuntimeProvider.getClassLoaders()).containsExactly(mock, RuntimeProvider.class.getClassLoader());
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	/**
	 * Verifies that only the class loader from {@link RuntimeProvider} will be returned, even if there is no thread
	 * context class loader.
	 */
	@Test
	public void classLoaderNotFromThread() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(null);
			assertThat(RuntimeProvider.getClassLoaders()).containsExactly(RuntimeProvider.class.getClassLoader());
		} finally {
			Thread.currentThread().setContextClassLoader(classLoader);
		}
	}

	/**
	 * Verifies that the environment can be detected without throwing any exception.
	 */
	@Test
	public void isAndroid() {
		RuntimeProvider.isAndroid();
	}

	/**
	 * Verifies that the name of a default writer will be returned.
	 */
	@Test
	public void defaultWriter() {
		assertThat(RuntimeProvider.getDefaultWriter()).isNotEmpty();
	}

	/**
	 * Verifies that the process ID will be returned.
	 */
	@Test
	public void processId() {
		assertThat(RuntimeProvider.getProcessId()).isEqualTo(ProcessHandle.current().pid());
	}

	/**
	 * Verifies that a valid start time will be returned.
	 *
	 * @throws InterruptedException
	 *             Interrupted while sleeping
	 */
	@Test
	public void startTime() throws InterruptedException {
		Timestamp first = RuntimeProvider.getStartTime();
		assertThat(first.toInstant()).isBefore(Instant.now());

		Thread.sleep(1);

		Timestamp second = RuntimeProvider.getStartTime();
		assertThat(second.toInstant()).isEqualTo(first.toInstant());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly, if depth in stack trace is
	 * defined as index.
	 */
	@Test
	public void callerClassNameByIndex() {
		assertThat(RuntimeProvider.getCallerClassName(1)).isEqualTo(RuntimeProviderTest.class.getName());
	}

	/**
	 * Verifies that the fully-qualified class name of a caller will be returned correctly, if successor in stack trace
	 * is defined.
	 */
	@Test
	public void callerClassNameBySuccessor() {
		assertThat(RuntimeProvider.getCallerClassName(RuntimeProvider.class.getName())).isEqualTo(RuntimeProviderTest.class.getName());
	}

	/**
	 * Verifies that the fully-qualified class name of an anonymous caller will be stripped.
	 */
	@Test
	public void anonymousCallerClassName() {
		new Object() {
			{
				assertThat(RuntimeProvider.getCallerClassName(1)).isEqualTo(RuntimeProviderTest.class.getName());
			}
		};
	}

	/**
	 * Verifies that pre-filled {@link StackTraceElement} will be returned, if stack trace does not contain the expected
	 * successor.
	 */
	@Test
	public void missingSuccessorForCallerClassName() {
		assertThat(RuntimeProvider.getCallerClassName(Logger.class.getName())).isEqualTo("<unknown class>");
		assertThat(systemStream.consumeErrorOutput()).contains(Logger.class.getName(), "missing");
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly, if depth in stack trace is
	 * defined as index.
	 */
	@Test
	public void callerStackTraceElementByIndex() {
		assertThat(RuntimeProvider.getCallerStackTraceElement(1)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that the complete stack trace element of a caller will be returned correctly, if successor in stack trace
	 * is defined.
	 */
	@Test
	public void callerStackTraceElementBySuccessor() {
		String className = RuntimeProvider.class.getName();
		assertThat(RuntimeProvider.getCallerStackTraceElement(className)).isEqualTo(new Throwable().getStackTrace()[0]);
	}

	/**
	 * Verifies that the anonymous part from the class name of a stack trace element will be stripped.
	 */
	@Test
	public void anonymousCallerStackTraceElement() {
		new Object() {
			{
				StackTraceElement currentElement = new Throwable().getStackTrace()[0];
				StackTraceElement fetchedElement = RuntimeProvider.getCallerStackTraceElement(1);
	
				assertThat(fetchedElement.getClassName()).isEqualTo(RuntimeProviderTest.class.getName());
				assertThat(fetchedElement.getMethodName()).isEqualTo(currentElement.getMethodName());
				assertThat(fetchedElement.getFileName()).isEqualTo(currentElement.getFileName());
				assertThat(fetchedElement.getLineNumber()).isEqualTo(currentElement.getLineNumber() + 1);
			}
		};
	}

	/**
	 * Verifies that pre-filled {@link StackTraceElement} will be returned, if stack trace does not contain the expected
	 * successor.
	 */
	@Test
	public void missingSuccessorForCallerStackTraceElement() {
		assertThat(RuntimeProvider.getCallerStackTraceElement(Logger.class.getName())).isEqualTo(
			new StackTraceElement("<unknown class>", "<unknown method>", "<unknown file>", -1)
		);
		assertThat(systemStream.consumeErrorOutput()).contains(Logger.class.getName(), "missing");
	}

	/**
	 * Verifies that named inner class name will be returned untouched.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#stripAnonymousPart(String)}
	 */
	@Test
	public void namedInnerClass() throws Exception {
		String className = Whitebox.invokeMethod(RuntimeProvider.class, "stripAnonymousPart", "MyClass$InnerClass");
		assertThat(className).isEqualTo("MyClass$InnerClass");
	}

	/**
	 * Verifies that anonymous inner classes will be cut off.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#stripAnonymousPart(String)}
	 */
	@Test
	public void anonymousInnerClass() throws Exception {
		String className = Whitebox.invokeMethod(RuntimeProvider.class, "stripAnonymousPart", "MyClass$1");
		assertThat(className).isEqualTo("MyClass");
	}

	/**
	 * Verifies that trailing dollars of Scala classes will be cut off.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#stripAnonymousPart(String)}
	 */
	@Test
	public void scalaObject() throws Exception {
		String className = Whitebox.invokeMethod(RuntimeProvider.class, "stripAnonymousPart", "ScalaObject$");
		assertThat(className).isEqualTo("ScalaObject");
	}

	/**
	 * Verifies that the anonymous part of Scala traits will be cut off.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#stripAnonymousPart(String)}
	 */
	@Test
	public void scalaTraits() throws Exception {
		String className = Whitebox.invokeMethod(RuntimeProvider.class, "stripAnonymousPart", "ScalaTrait$class");
		assertThat(className).isEqualTo("ScalaTrait");
	}

	/**
	 * Verifies that the anonymous part of Groovy closures will be cut off.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#stripAnonymousPart(String)}
	 */
	@Test
	public void groovyClosure() throws Exception {
		String className = Whitebox.invokeMethod(RuntimeProvider.class, "stripAnonymousPart", "groovyClosure$_runImpl_closure_1");
		assertThat(className).isEqualTo("groovyClosure");
	}

	/**
	 * Verifies that correct timestamps will be created.
	 *
	 * @throws InterruptedException
	 *             Interrupted while waiting between creation of both timestamps
	 */
	@Test
	public void createTimestamp() throws InterruptedException {
		Timestamp timestamp = RuntimeProvider.createTimestamp();
		assertThat(timestamp.toInstant()).isBetween(Instant.now().minusSeconds(1), Instant.now());

		Thread.sleep(2);

		assertThat(RuntimeProvider.createTimestamp().toInstant()).isAfter(timestamp.toInstant());
	}

	/**
	 * Verifies that a correct timestamp formatter will be created.
	 */
	@Test
	public void createTimestampFormatter() {
		TimestampFormatter formatter = RuntimeProvider.createTimestampFormatter("yyyy-MM-dd HH:mm", Locale.US);
		Timestamp timestamp = TimestampFactory.create(1985, 6, 3, 12, 30);
		assertThat(formatter.format(timestamp)).isEqualTo("1985-06-03 12:30");
	}

	/**
	 * Verifies that {@link AndroidRuntime} will be resolved as runtime dialect in Android Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectAndroidRuntime() throws Exception {
		System.setProperty("java.runtime.name", "Android Runtime");
		System.setProperty("java.version", "0");

		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(AndroidRuntime.class);
	}

	/**
	 * Verifies that {@link LegacyJavaRuntime} will be resolved as runtime dialect in Java 6 Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectJava6Runtime() throws Exception {
		System.setProperty("java.runtime.name", "OpenJDK Runtime Environment");
		System.setProperty("java.version", "1.6.0_23");

		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(LegacyJavaRuntime.class);
	}

	/**
	 * Verifies that {@link LegacyJavaRuntime} will be resolved as runtime dialect in Java 8 Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectJava8Runtime() throws Exception {
		System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
		System.setProperty("java.version", "1.8.0_111");

		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(LegacyJavaRuntime.class);
	}

	/**
	 * Verifies that {@link ModernJavaRuntime} will be resolved as runtime dialect in Java 9 Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectJava9Runtime() throws Exception {
		System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
		System.setProperty("java.version", "9.0.4");

		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(ModernJavaRuntime.class);
	}

}
