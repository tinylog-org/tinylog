/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.writers;

import java.util.Map;

import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tinylog.Level;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.core.LogEntryValue;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.LogEntryBuilder;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.tinylog.util.Maps.doubletonMap;
import static org.tinylog.util.Maps.tripletonMap;

/**
 * Tests for {@link LogcatWriter}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Log.class)
public final class LogcatWriterTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	private ArgumentCaptor<Integer> levelCaptor;
	private ArgumentCaptor<String> tagCaptor;
	private ArgumentCaptor<String> messageCaptor;

	/**
	 * Mocks Android's Log class and captures all issued log entries.
	 */
	@Before
	public void mockLog() {
		levelCaptor = ArgumentCaptor.forClass(Integer.class);
		tagCaptor = ArgumentCaptor.forClass(String.class);
		messageCaptor = ArgumentCaptor.forClass(String.class);

		mockStatic(Log.class);

		when(Log.println(levelCaptor.capture(), tagCaptor.capture(), messageCaptor.capture())).thenReturn(0);
	}

	/**
	 * Verifies that all required log entry values will be detected.
	 */
	@Test
	public void requiredLogEntryValues() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "{thread}", "format", "{class}: {message}"));
		assertThat(writer.getRequiredLogEntryValues())
			.containsOnly(LogEntryValue.LEVEL, LogEntryValue.THREAD, LogEntryValue.CLASS, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);
	}

	/**
	 * Verifies that the simple class name is used as tag and the message including exception as log message by default.
	 */
	@Test
	public void defaultFormatPatterns() {
		LogcatWriter writer = new LogcatWriter(emptyMap());

		assertThat(writer.getRequiredLogEntryValues())
			.containsOnly(LogEntryValue.LEVEL, LogEntryValue.CLASS, LogEntryValue.MESSAGE, LogEntryValue.EXCEPTION);

		writer.write(LogEntryBuilder.prefilled(LogcatWriterTest.class).create());
		writer.close();

		assertThat(levelCaptor.getAllValues()).containsOnly(Log.VERBOSE);
		assertThat(tagCaptor.getAllValues()).containsOnly(LogcatWriterTest.class.getSimpleName());
		assertThat(messageCaptor.getAllValues()).containsOnly(LogEntryBuilder.DEFAULT_MESSAGE);
	}

	/**
	 * Verifies that a trace log entry will be redirected to Android's Log class.
	 */
	@Test
	public void trace() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "{class-name}", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.TRACE).className("MyClass").message("Hello World!").create());

		assertThat(levelCaptor.getAllValues()).containsOnly(Log.VERBOSE);
		assertThat(tagCaptor.getAllValues()).containsOnly("MyClass");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!");
	}

	/**
	 * Verifies that a debug log entry will be redirected to Android's Log class.
	 */
	@Test
	public void debug() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "{class-name}", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.DEBUG).className("MyClass").message("Hello World!").create());

		assertThat(levelCaptor.getAllValues()).containsOnly(Log.DEBUG);
		assertThat(tagCaptor.getAllValues()).containsOnly("MyClass");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!");
	}

	/**
	 * Verifies that an info log entry will be redirected to Android's Log class.
	 */
	@Test
	public void info() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "{class-name}", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.INFO).className("MyClass").message("Hello World!").create());

		assertThat(levelCaptor.getAllValues()).containsOnly(Log.INFO);
		assertThat(tagCaptor.getAllValues()).containsOnly("MyClass");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!");
	}

	/**
	 * Verifies that a warning log entry will be redirected to Android's Log class.
	 */
	@Test
	public void warning() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "{class-name}", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.WARN).className("MyClass").message("Hello World!").create());

		assertThat(levelCaptor.getAllValues()).containsOnly(Log.WARN);
		assertThat(tagCaptor.getAllValues()).containsOnly("MyClass");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!");
	}

	/**
	 * Verifies that an error log entry will be redirected to Android's Log class.
	 */
	@Test
	public void error() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "{class-name}", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.ERROR).className("MyClass").message("Hello World!").create());

		assertThat(levelCaptor.getAllValues()).containsOnly(Log.ERROR);
		assertThat(tagCaptor.getAllValues()).containsOnly("MyClass");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!");
	}

	/**
	 * Verifies that too long tags will be trimmed to 23 characters.
	 */
	@Test
	public void trimTag() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.INFO).message("Hello World!").create());

		assertThat(tagCaptor.getAllValues()).containsOnly("ABCDEFGHIJKLMNOPQRST...");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!");
	}

	/**
	 * Verifies that the writer works correctly, if performance optimizations for writing thread are enabled.
	 */
	@Test
	public void writingThread() {
		LogcatWriter writer = new LogcatWriter(tripletonMap("tagname", "{class-name}", "format", "{message}", "writingthread", "true"));
		writer.write(LogEntryBuilder.empty().level(Level.INFO).className("FirstClass").message("Hello World!").create());
		writer.write(LogEntryBuilder.empty().level(Level.INFO).className("SecondClass").message("Hello Universe!").create());

		assertThat(tagCaptor.getAllValues()).containsOnly("FirstClass", "SecondClass");
		assertThat(messageCaptor.getAllValues()).containsOnly("Hello World!", "Hello Universe!");
	}

	/**
	 * Verifies that an illegal severity level will be reported.
	 */
	@Test
	public void illegalSeverityLevel() {
		LogcatWriter writer = new LogcatWriter(doubletonMap("tagname", "STATIC", "format", "{message}"));
		writer.write(LogEntryBuilder.empty().level(Level.OFF).message("Hello World!").create());

		assertThat(tagCaptor.getAllValues()).isEmpty();
		assertThat(messageCaptor.getAllValues()).isEmpty();

		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("OFF");
	}

	/**
	 * Verifies that writer is registered as service under the name "logcat".
	 */
	@Test
	public void isRegistered() {
		Writer writer = new ServiceLoader<Writer>(Writer.class, Map.class).create("logcat", emptyMap());
		assertThat(writer).isInstanceOf(LogcatWriter.class);
	}

}
