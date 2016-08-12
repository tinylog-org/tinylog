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

package org.pmw.tinylog.writers;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pmw.tinylog.hamcrest.CollectionMatchers.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.util.LogEntryBuilder;
import org.pmw.tinylog.util.PropertiesBuilder;

import android.util.Log;
import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for Android's logcat writer.
 *
 * @see LogcatWriter
 */
public class LogcatWriterTest extends AbstractWriterTest {

	/**
	 * Test initialization of the writer with existing {@link android.util.Log} class.
	 */
	@Test
	public final void testInitOnAndroid() {
		new MockUp<Class<?>>() {

			@Mock
			public Class<?> forName(final String className) throws ClassNotFoundException {
				return "android.util.Log".equals(className) ? null : Class.forName(className);
			}

		};

		LogcatWriter writer = new LogcatWriter();
		writer.init(null);
		writer.close();
	}

	/**
	 * Test initialization of the writer if {@link android.util.Log} class is missing.
	 */
	@Test
	public final void testInitOnJava() {
		new MockUp<Class<?>>() {

			@Mock
			public Class<?> forName(final String className) throws ClassNotFoundException {
				if ("android.util.Log".equals(className)) {
					throw new ClassNotFoundException();
				} else {
					return Class.forName(className);
				}
			}

		};

		LogcatWriter writer = new LogcatWriter();
		writer.init(null);
		assertEquals("LOGGER ERROR: Logcat writer works only on Android", getErrorStream().nextLine());
		writer.close();
	}

	/**
	 * Test required log entry values.
	 */
	@Test
	public final void testRequiredLogEntryValues() {
		Set<LogEntryValue> requiredValues = new LogcatWriter().getRequiredLogEntryValues();
		assertThat(requiredValues, containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY));

		requiredValues = new LogcatWriter("example").getRequiredLogEntryValues();
		assertThat(requiredValues, containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY));
	}

	/**
	 * Test trimming too long tags.
	 */
	@Test
	public final void testTrimmingTag() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("A_VERY_VERY_LONG_IDNETIFIER");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry("Message\n").create());
			assertThat(mock.consume(), contains("i/A_VERY_VERY_LONG_IDN.../Message\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test using class names as tag.
	 */
	@Test
	public final void testClassAsTag() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter();
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.INFO).className("MyClass").renderedLogEntry("Message\n").create());
			assertThat(mock.consume(), contains("i/MyClass/Message\n"));

			writer.write(new LogEntryBuilder().level(Level.INFO).className("com.example.AnotherClass").renderedLogEntry("Message\n").create());
			assertThat(mock.consume(), contains("i/AnotherClass/Message\n"));

			writer.write(new LogEntryBuilder().level(Level.INFO).className("AVeryVeryLongNameForAClass").renderedLogEntry("Message\n").create());
			assertThat(mock.consume(), contains("i/AVeryVeryLongNameFor.../Message\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test writing trace log entries.
	 */
	@Test
	public final void testTrace() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("Test");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.TRACE).renderedLogEntry("Hello\n").create());
			assertThat(mock.consume(), contains("v/Test/Hello\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test writing debug log entries.
	 */
	@Test
	public final void testDebug() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("Test");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.DEBUG).renderedLogEntry("Hello\n").create());
			assertThat(mock.consume(), contains("d/Test/Hello\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test writing info log entries.
	 */
	@Test
	public final void testInfo() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("Test");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.INFO).renderedLogEntry("Hello\n").create());
			assertThat(mock.consume(), contains("i/Test/Hello\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test writing warning log entries.
	 */
	@Test
	public final void testWarning() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("Test");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.WARNING).renderedLogEntry("Hello\n").create());
			assertThat(mock.consume(), contains("w/Test/Hello\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test writing error log entries.
	 */
	@Test
	public final void testError() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("Test");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.ERROR).renderedLogEntry("Hello\n").create());
			assertThat(mock.consume(), contains("e/Test/Hello\n"));

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test handling log entries with invalid logging level.
	 */
	@Test
	public final void testInvalidLevel() {
		LogcatMock mock = new LogcatMock();
		try {
			LogcatWriter writer = new LogcatWriter("Test");
			writer.init(null);

			writer.write(new LogEntryBuilder().level(Level.OFF).renderedLogEntry("Hello\n").create());
			assertThat(mock.consume(), empty());
			assertEquals("LOGGER WARNING: Unexpected logging level: OFF", getErrorStream().nextLine());

			writer.close();
		} finally {
			mock.tearDown();
		}
	}

	/**
	 * Test reading logcat writer from properties.
	 */
	@Test
	public final void testFromProperties() {
		PropertiesBuilder propertiesBuilder = new PropertiesBuilder().set("tinylog.writer", "logcat");
		List<Writer> writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(LogcatWriter.class));
		Set<LogEntryValue> requiredValues = writers.get(0).getRequiredLogEntryValues();
		assertThat(requiredValues, containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.RENDERED_LOG_ENTRY));

		propertiesBuilder.set("tinylog.writer.tag", "TEST");
		writers = createFromProperties(propertiesBuilder.create());
		assertThat(writers, types(LogcatWriter.class));
		requiredValues = writers.get(0).getRequiredLogEntryValues();
		assertThat(requiredValues, containsInAnyOrder(LogEntryValue.LEVEL, LogEntryValue.RENDERED_LOG_ENTRY));
	}

	private static final class LogcatMock extends MockUp<Log> {

		private final List<String> entries;

		private LogcatMock() {
			entries = new ArrayList<>();
		}

		public List<String> consume() {
			List<String> copy = new ArrayList<>(entries);
			entries.clear();
			return copy;
		}

		@Mock
		public int v(final String tag, final String msg) {
			entries.add("v/" + tag + "/" + msg);
			return 0;
		}

		@Mock
		public int d(final String tag, final String msg) {
			entries.add("d/" + tag + "/" + msg);
			return 0;
		}

		@Mock
		public int i(final String tag, final String msg) {
			entries.add("i/" + tag + "/" + msg);
			return 0;
		}

		@Mock
		public int w(final String tag, final String msg) {
			entries.add("w/" + tag + "/" + msg);
			return 0;
		}

		@Mock
		public int e(final String tag, final String msg) {
			entries.add("e/" + tag + "/" + msg);
			return 0;
		}

	}

}
