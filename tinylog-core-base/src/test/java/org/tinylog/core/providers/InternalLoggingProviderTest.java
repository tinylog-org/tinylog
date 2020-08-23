/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.providers;

import java.util.Locale;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.EnhancedMessageFormatter;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemErr;
import static org.assertj.core.api.Assertions.assertThat;

class InternalLoggingProviderTest {

	/**
	 * Verifies that a plain text message can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void plainTextMessage(Level level) throws Exception {
		String output = tapSystemErr(() -> new InternalLoggingProvider().log(
			null,
			"tinylog",
			level,
			null,
			"Hello World!",
			null,
			null
		));

		assertThat(output).isEqualTo("TINYLOG " + level + ": Hello World!" + System.lineSeparator());
	}

	/**
	 * Verifies that a formatted text message with placeholders can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void formattedTextMessage(Level level) throws Exception {
		String output = tapSystemErr(() -> new InternalLoggingProvider().log(
			null,
			"tinylog",
			level,
			null,
			"Hello {}!",
			new Object[] {"world"},
			new EnhancedMessageFormatter(new Framework(), Locale.ENGLISH)
		));

		assertThat(output).isEqualTo("TINYLOG " + level + ": Hello world!" + System.lineSeparator());
	}

	/**
	 * Verifies that an exception can be output at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void exceptionOnly(Level level) throws Exception {
		Exception exception = new NullPointerException();
		exception.setStackTrace(new StackTraceElement[] {
			new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42),
			new StackTraceElement("example.OtherClass", "bar", "OtherClass.java", 42),
		});

		String output = tapSystemErr(() -> new InternalLoggingProvider().log(
			null,
			"tinylog",
			level,
			exception,
			null,
			null,
			null
		));

		assertThat(output).isEqualTo(
			"TINYLOG " + level + ": java.lang.NullPointerException" + System.lineSeparator()
			+ "\tat example.MyClass.foo(MyClass.java:42)" + System.lineSeparator()
			+ "\tat example.OtherClass.bar(OtherClass.java:42)" + System.lineSeparator()
		);
	}

	/**
	 * Verifies that an exception can be output together with a custom message at the severity levels warn and error.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"WARN", "ERROR"})
	public void exceptionWithCustomMessage(Level level) throws Exception {
		Exception exception = new UnsupportedOperationException();
		exception.setStackTrace(new StackTraceElement[] {
			new StackTraceElement("example.MyClass", "foo", "MyClass.java", 42)
		});

		String output = tapSystemErr(() -> new InternalLoggingProvider().log(
			null,
			"tinylog",
			level,
			exception,
			"Oops!",
			null,
			null
		));

		assertThat(output).isEqualTo(
			"TINYLOG " + level + ": Oops!: java.lang.UnsupportedOperationException" + System.lineSeparator()
				+ "\tat example.MyClass.foo(MyClass.java:42)" + System.lineSeparator()
		);
	}

	/**
	 * Verifies that log entries are discarded for the severity levels trace, debug, and info.
	 *
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@EnumSource(value = Level.class, names = {"TRACE", "DEBUG", "INFO"})
	public void discardNonServeLogEntries(Level level) throws Exception {
		String output = tapSystemErr(() -> new InternalLoggingProvider().log(
			null,
			"tinylog",
			level,
			null,
			"Hello World!",
			null,
			null
		));

		assertThat(output).isEmpty();
	}

	/**
	 * Verifies that log entries will be discarded, if tag is not {@code tinylog}.
	 *
	 * @param tag The category tag for the log entry
	 * @param level The severity level for the log entry
	 */
	@ParameterizedTest
	@CsvSource({",ERROR", "foo,ERROR", ",WARN", "foo,WARN"})
	public void discardNonTinylogLogEntries(String tag, Level level) throws Exception {
		String output = tapSystemErr(() -> new InternalLoggingProvider().log(
			null,
			tag,
			level,
			null,
			"Hello World!",
			null,
			null
		));

		assertThat(output).isEmpty();
	}

}
