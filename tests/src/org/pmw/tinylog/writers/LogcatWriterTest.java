/*
 * Copyright 2014 Martin Winandy
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
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.EnvironmentHelper;
import org.pmw.tinylog.LoggingLevel;
import org.pmw.tinylog.util.LogEntryBuilder;

import android.util.Log;

/**
 * Tests for the logcat logging writer for Android.
 * 
 * @see LogcatWriter
 */
public class LogcatWriterTest extends AbstractTest {

	/**
	 * Test required log entry values.
	 */
	@Test
	public final void testRequiredLogEntryValue() {
		Set<LogEntryValue> requiredLogEntryValues = new LogcatWriter().getRequiredLogEntryValues();
		assertThat(requiredLogEntryValues, contains(LogEntryValue.CLASS, LogEntryValue.LOGGING_LEVEL, LogEntryValue.RENDERED_LOG_ENTRY));
	}

	/**
	 * Test logging.
	 */
	@Test
	public final void testLogging() {
		String newLine = EnvironmentHelper.getNewLine();

		LogcatWriter writer = new LogcatWriter();
		writer.init(null);

		assertEquals(0, Log.consumeEntries().size());

		writer.write(new LogEntryBuilder().level(LoggingLevel.TRACE).className("com.package.MyClass").renderedLogEntry("Hello World" + newLine).create());
		assertThat(Log.consumeEntries(), is(Collections.singletonList("V\tMyClass\tHello World")));

		writer.write(new LogEntryBuilder().level(LoggingLevel.DEBUG).className("a.b.MyClass").renderedLogEntry("Hello World" + newLine).create());
		assertThat(Log.consumeEntries(), is(Collections.singletonList("D\tMyClass\tHello World")));

		writer.write(new LogEntryBuilder().level(LoggingLevel.INFO).className("MyClass").renderedLogEntry("Hello World" + newLine).create());
		assertThat(Log.consumeEntries(), is(Collections.singletonList("I\tMyClass\tHello World")));

		writer.write(new LogEntryBuilder().level(LoggingLevel.WARNING).className("com.package.MyClass").renderedLogEntry("Hello World" + newLine).create());
		assertThat(Log.consumeEntries(), is(Collections.singletonList("W\tMyClass\tHello World")));

		writer.write(new LogEntryBuilder().level(LoggingLevel.ERROR).className("com.package.MyClass").renderedLogEntry("Hello World").create());
		assertThat(Log.consumeEntries(), is(Collections.singletonList("E\tMyClass\tHello World")));

		try {
			writer.write(new LogEntryBuilder().level(LoggingLevel.OFF).className("com.package.MyClass").renderedLogEntry("Hello World").create());
			fail("IllegalArgumentException expected");
		} catch (IllegalArgumentException ex) {
			// Expected
		}

		writer.close();
	}

	/**
	 * Test flushing.
	 */
	@Test
	public final void testFlush() {
		LogcatWriter writer = new LogcatWriter();
		writer.init(null);

		writer.write(new LogEntryBuilder().level(LoggingLevel.INFO).className("MyClass").renderedLogEntry("Hello").create());
		writer.flush();

		assertThat(Log.consumeEntries(), is(Collections.singletonList("I\tMyClass\tHello")));

		writer.close();
	}

}
