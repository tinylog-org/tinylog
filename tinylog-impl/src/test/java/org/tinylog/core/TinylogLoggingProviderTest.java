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

package org.tinylog.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.assertj.core.api.Condition;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.EvilWriter;
import org.tinylog.util.StorageWriter;
import org.tinylog.util.Strings;
import org.tinylog.writers.ConsoleWriter;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.tinylog.util.Maps.doubletonMap;
import static org.tinylog.util.ResultObserver.waitFor;

/**
 * Tests for {@link TinylogLoggingProvider}.
 */
@RunWith(Enclosed.class)
public final class TinylogLoggingProviderTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Tests for default configuration (trace severity level and console writer).
	 */
	@RunWith(Parameterized.class)
	public static final class DefaultConfiguration extends AbstractTaggedTest {

		/**
		 * @param name
		 *            Human-readable test name
		 * @param tag
		 *            Tag under test
		 */
		public DefaultConfiguration(final String name, final String tag) {
			super(tag);
		}

		/**
		 * Verifies that the minimum severity level is {@link Level#TRACE}.
		 */
		@Test
		public void minimumLevel() {
			assertThat(provider.getMinimumLevel(tag)).isEqualTo(Level.TRACE);
		}

		/**
		 * Verifies that trace severity level is enabled and log entries will be output.
		 */
		@Test
		public void traceEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.TRACE)).isTrue();

			provider.log(1, tag, Level.TRACE, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce(Level.TRACE.toString()).containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that debug severity level is enabled and log entries will be output.
		 */
		@Test
		public void debugEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.DEBUG)).isTrue();

			provider.log(1, tag, Level.DEBUG, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce(Level.DEBUG.toString()).containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output.
		 */
		@Test
		public void infoEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.INFO)).isTrue();

			provider.log(1, tag, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce(Level.INFO.toString()).containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output.
		 */
		@Test
		public void warningEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.WARNING)).isTrue();

			provider.log(1, tag, Level.WARNING, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce(Level.WARNING.toString()).containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output.
		 */
		@Test
		public void errorEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.ERROR)).isTrue();

			provider.log(1, tag, Level.ERROR, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce(Level.ERROR.toString()).containsOnlyOnce("Hello World!");
		}
	}

	/**
	 * Tests for disabled logging (severity level is {@link Level#OFF}).
	 */
	@RunWith(Parameterized.class)
	public static final class DisabledLogging extends AbstractTaggedTest {

		/**
		 * @param name
		 *            Human-readable test name
		 * @param tag
		 *            Tag under test
		 */
		public DisabledLogging(final String name, final String tag) {
			super(tag);
		}

		/**
		 * Disables logging.
		 */
		@BeforeClass
		public static void configure() {
			Configuration.replace(singletonMap("level", "off"));
		}

		/**
		 * Verifies that the minimum severity level is {@link Level#OFF}.
		 */
		@Test
		public void minimumLevel() {
			assertThat(provider.getMinimumLevel(tag)).isEqualTo(Level.OFF);
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output.
		 */
		@Test
		public void traceDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.TRACE)).isFalse();

			provider.log(1, tag, Level.TRACE, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that debug severity level is disabled and no log entries will be output.
		 */
		@Test
		public void debugDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.DEBUG)).isFalse();

			provider.log(1, tag, Level.DEBUG, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that info severity level is disabled and no log entries will be output.
		 */
		@Test
		public void infoDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.INFO)).isFalse();

			provider.log(1, tag, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that warning severity level is disabled and no log entries will be output.
		 */
		@Test
		public void warningDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.WARNING)).isFalse();

			provider.log(1, tag, Level.WARNING, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEmpty();
		}

		/**
		 * Verifies that error severity level is disabled and no log entries will be output.
		 */
		@Test
		public void errorDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.ERROR)).isFalse();

			provider.log(1, tag, Level.ERROR, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEmpty();
		}

	}

	/**
	 * Tests for custom severity level for a class (custom severity level is {@link Level#DEBUG} and global severity
	 * level is {@link Level#WARNING}).
	 */
	@RunWith(Parameterized.class)
	public static final class CustomSeverityLevelForClass extends AbstractTaggedTest {

		private static final int DEPTH_INNER_CLASS = 1;
		private static final int DEPTH_OUTER_CLASS = 2;

		/**
		 * @param name
		 *            Human-readable test name
		 * @param tag
		 *            Tag under test
		 */
		public CustomSeverityLevelForClass(final String name, final String tag) {
			super(tag);
		}

		/**
		 * Activates {@link ConsoleWriter} and sets severity levels.
		 */
		@BeforeClass
		public static void configure() {
			Configuration.replace(emptyMap());

			Configuration.set("writer", "console");
			Configuration.set("writer.format", "{level}: {message}");

			Configuration.set("level", "warning");
			Configuration.set("level@" + CustomSeverityLevelForClass.class.getName(), "debug");
		}

		/**
		 * Verifies that the minimum severity level is {@link Level#DEBUG}.
		 */
		@Test
		public void minimumLevel() {
			assertThat(provider.getMinimumLevel(tag)).isEqualTo(Level.DEBUG);
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output for inner class.
		 */
		@Test
		public void traceDisabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.TRACE)).isFalse();

			provider.log(DEPTH_INNER_CLASS, tag, Level.TRACE, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output for outer class.
		 */
		@Test
		public void traceDisabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.TRACE)).isFalse();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.TRACE, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that debug severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void debugEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.DEBUG)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.DEBUG, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.DEBUG + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that debug severity level is disabled and no log entries will be output for outer class.
		 */
		@Test
		public void debugDisabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.DEBUG)).isFalse();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.DEBUG, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void infoEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.INFO)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that info severity level is disabled and no log entries will be output for outer class.
		 */
		@Test
		public void infoDisabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.INFO)).isFalse();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void warningEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.WARNING)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.WARNING, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARNING + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output for outer class.
		 */
		@Test
		public void warningEnabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.WARNING)).isTrue();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.WARNING, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARNING + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void errorEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.ERROR)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.ERROR, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output for outer class.
		 */
		@Test
		public void errorEnabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.ERROR)).isTrue();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.ERROR, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

	}

	/**
	 * Tests for custom severity level for a tag (custom severity level is {@link Level#INFO} and global severity level
	 * is {@link Level#TRACE}).
	 */
	public static final class CustomSeverityLevelForTag extends AbstractTest {

		/**
		 * Sets severity levels.
		 */
		@BeforeClass
		public static void configure() {
			Configuration.replace(emptyMap());

			Configuration.set("writer1", "console");
			Configuration.set("writer1.tag", "-");
			Configuration.set("writer1.format", "{level}: {message}");

			Configuration.set("writer2", "console");
			Configuration.set("writer2.level", "info");
			Configuration.set("writer2.tag", "test");
			Configuration.set("writer2.format", "{level}: {message}");
		}

		/**
		 * Verifies that the minimum severity level for an untagged logger is {@link Level#TRACE}.
		 */
		@Test
		public void untaggedMinimumLevel() {
			assertThat(provider.getMinimumLevel(null)).isEqualTo(Level.TRACE);
		}

		/**
		 * Verifies that the minimum severity level for a tagged logger is {@link Level#INFO}.
		 */
		@Test
		public void taggedMinimumLevel() {
			assertThat(provider.getMinimumLevel("test")).isEqualTo(Level.INFO);
		}

		/**
		 * Verifies that trace severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedTraceEnabled() {
			assertThat(provider.isEnabled(1, null, Level.TRACE)).isTrue();

			provider.log(1, null, Level.TRACE, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.TRACE + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output if tagged.
		 */
		@Test
		public void taggedTraceDisabled() {
			assertThat(provider.isEnabled(1, "test", Level.TRACE)).isFalse();

			provider.log(1, "test", Level.TRACE, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that debug severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedDebugEnabled() {
			assertThat(provider.isEnabled(1, null, Level.DEBUG)).isTrue();

			provider.log(1, null, Level.DEBUG, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.DEBUG + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that debug severity level is disabled and no log entries will be output if tagged.
		 */
		@Test
		public void taggedDebugDisabled() {
			assertThat(provider.isEnabled(1, "test", Level.DEBUG)).isFalse();

			provider.log(1, "test", Level.DEBUG, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedInfoEnabled() {
			assertThat(provider.isEnabled(1, null, Level.INFO)).isTrue();

			provider.log(1, null, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output if tagged.
		 */
		@Test
		public void taggedInfoEnabled() {
			assertThat(provider.isEnabled(1, "test", Level.INFO)).isTrue();

			provider.log(1, "test", Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedWarningEnabled() {
			assertThat(provider.isEnabled(1, null, Level.WARNING)).isTrue();

			provider.log(1, null, Level.WARNING, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARNING + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output if tagged.
		 */
		@Test
		public void taggedWarningEnabled() {
			assertThat(provider.isEnabled(1, "test", Level.WARNING)).isTrue();

			provider.log(1, "test", Level.WARNING, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARNING + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedErrorEnabled() {
			assertThat(provider.isEnabled(1, null, Level.ERROR)).isTrue();

			provider.log(1, null, Level.ERROR, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output if tagged.
		 */
		@Test
		public void taggedErrorEnabled() {
			assertThat(provider.isEnabled(1, "test", Level.ERROR)).isTrue();

			provider.log(1, "test", Level.ERROR, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

	}

	/**
	 * Tests for receiving context provider.
	 */
	public static final class ContextProvider extends AbstractTest {

		/**
		 * Verifies that the correct context provider will be returned.
		 */
		@Test
		public void type() {
			assertThat(provider.getContextProvider()).isInstanceOf(TinylogContextProvider.class);
		}

	}

	/**
	 * Tests for logging with enabled writing thread.
	 */
	public static final class WritingThreadEnabled extends AbstractTest {

		private static final Condition<Thread> writingThread = new Condition<>(WritingThread.class::isInstance, "WritingThread");

		/**
		 * Activates writing thread and disables auto shutdown.
		 */
		@BeforeClass
		public static void configure() {
			Configuration.replace(doubletonMap("writingthread", "true", "autoshutdown", "false"));
		}

		/**
		 * Shuts down the logging provider.
		 */
		@After
		public void shutdown() {
			provider.shutdown();
			
			waitFor(() -> Thread.getAllStackTraces().keySet(),
					threads -> threads.stream().filter(WritingThread.class::isInstance).count() == 0,
					1000);
			
			assertThat(Thread.getAllStackTraces().keySet()).doNotHave(writingThread);
		}

		/**
		 * Verifies that writing thread is running.
		 */
		@Test
		public void running() {
			assertThat(Thread.getAllStackTraces().keySet()).haveExactly(1, writingThread);
		}

		/**
		 * Verifies that log entries will be output.
		 */
		@Test
		public void logging() {
			provider.log(1, null, Level.INFO, null, "Hello World!");
			String output = waitFor(systemStream::consumeStandardOutput, Strings::isNeitherNullNorEmpty, 100);
			assertThat(output).containsOnlyOnce(Level.INFO.toString()).containsOnlyOnce("Hello World!");
		}

	}

	/**
	 * Tests for writer operations that throw an exception.
	 */
	public static final class WritingFailed extends AbstractTest {

		/**
		 * Activates {@link EvilWriter} and disables auto shutdown.
		 */
		@BeforeClass
		public static void configure() {
			Configuration.replace(doubletonMap("writer", EvilWriter.class.getName(), "autoshutdown", "false"));
		}

		/**
		 * Verifies that a thrown exception will be reported while writing.
		 */
		@Test
		public void logging() {
			provider.log(1, null, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput())
					.containsOnlyOnce("ERROR")
					.containsOnlyOnce(IOException.class.getName())
					.containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that a thrown exception will be reported while shutting down a writer.
		 */
		@Test
		public void shutdown() {
			provider.shutdown();
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce(IOException.class.getName());
		}

	}

	/**
	 * Tests for integrity of created log entries.
	 */
	public static final class LogEntryValues {

		/**
		 * Activates {@link StorageWriter}.
		 */
		@Before
		public void configure() {
			Configuration.replace(singletonMap("writer", StorageWriter.class.getName()));
		}

		/**
		 * Clears global tinylog configuration.
		 */
		@After
		public void clear() {
			Configuration.replace(emptyMap());
		}

		/**
		 * Verifies that date of issue is present in received log entry.
		 */
		@Test
		public void date() {
			Configuration.set("writer.values", "date");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).hasSize(1).extracting(LogEntry::getDate).allSatisfy(date -> {
				assertThat(date).isCloseTo(new Date(), 1000);
			});
		}

		/**
		 * Verifies that the current thread is present in received log entry.
		 */
		@Test
		public void thread() {
			Configuration.set("writer.values", "thread");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getThread).containsOnly(Thread.currentThread());
		}

		/**
		 * Verifies that set context values are present in received log entry.
		 */
		@SuppressWarnings("unchecked")
		@Test
		public void context() {
			Configuration.set("writer.values", "context");

			TinylogLoggingProvider provider = new TinylogLoggingProvider();
			provider.getContextProvider().put("test", "42");
			provider.log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getContext).containsOnly(singletonMap("test", "42"));
		}

		/**
		 * Verifies that the issuing class name is present in received log entry.
		 */
		@Test
		public void className() {
			Configuration.set("writer.values", "class");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getClassName).containsOnly(LogEntryValues.class.getName());
		}

		/**
		 * Verifies that the issuing method name is present in received log entry.
		 */
		@Test
		public void methodName() {
			Configuration.set("writer.values", "method");

			String method = new Throwable().getStackTrace()[0].getMethodName();
			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getMethodName).containsOnly(method);
		}

		/**
		 * Verifies that the issuing source file name is present in received log entry.
		 */
		@Test
		public void fileName() {
			Configuration.set("writer.values", "file");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries())
					.extracting(LogEntry::getFileName)
					.containsOnly(TinylogLoggingProviderTest.class.getSimpleName() + ".java");
		}

		/**
		 * Verifies that the issuing line number in source file is present in received log entry.
		 */
		@Test
		public void lineNumber() {
			Configuration.set("writer.values", "file");

			int lineNumber = new Throwable().getStackTrace()[0].getLineNumber() + 1;
			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getLineNumber).containsOnly(lineNumber);
		}

		/**
		 * Verifies that a tag is present in received log entry.
		 */
		@Test
		public void tag() {
			Configuration.set("writer.values", "tag");

			new TinylogLoggingProvider().log(1, "test", Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getTag).containsOnly("test");
		}

		/**
		 * Verifies that severity level is present in received log entry.
		 */
		@Test
		public void level() {
			Configuration.set("writer.values", "level");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getLevel).containsOnly(Level.INFO);
		}

		/**
		 * Verifies that logged text message is present in received log entry.
		 */
		@Test
		public void message() {
			Configuration.set("writer.values", "message");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, "Hello World!");

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getMessage).containsOnly("Hello World!");
		}

		/**
		 * Verifies that logged exception is present in received log entry.
		 */
		@Test
		public void exception() {
			Configuration.set("writer.values", "exception");

			Exception exception = new NullPointerException();
			new TinylogLoggingProvider().log(1, null, Level.INFO, exception, null);

			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getException).containsOnly(exception);
		}

	}

	/**
	 * Tests for the different types of messages to log.
	 */
	public static final class LogMessages extends AbstractTest {

		/**
		 * Activates {@link ConsoleWriter} with plain message as format pattern.
		 */
		@BeforeClass
		public static void configure() {
			Configuration.replace(doubletonMap("writer", "console", "writer.format", "{message}"));
		}

		/**
		 * Verifies that any kind of objects can be logged, even if they don't implement {@link CharSequence}.
		 */
		@Test
		public void object() {
			provider.log(1, null, Level.INFO, null, 42);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("42" + NEW_LINE);
		}

		/**
		 * Verifies that a plain text message can be logged.
		 */
		@Test
		public void plainText() {
			provider.log(1, null, Level.INFO, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that a text message with placeholders can be logged.
		 */
		@Test
		public void arguments() {
			provider.log(1, null, Level.INFO, null, "Hello {}!", 42);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("Hello 42!" + NEW_LINE);
		}

	}

	/**
	 * Base class for inner parameterized test classes with tags.
	 */
	protected abstract static class AbstractTaggedTest extends AbstractTest {

		protected final String tag;

		/**
		 * @param tag
		 *            Tag under test
		 */
		private AbstractTaggedTest(final String tag) {
			this.tag = tag;
		}

		/**
		 * Provides all tags that should be tested. The first element of each array is the human-readable test name and
		 * the second the real tag.
		 *
		 * @return Tags under test
		 */
		@Parameters(name = "{0}")
		public static Collection<Object[]> getTags() {
			return Arrays.asList(new Object[] { "untagged", null }, new Object[] { "test tag", "test" });
		}

	}

	/**
	 * Base class for inner test classes.
	 */
	protected abstract static class AbstractTest {

		/**
		 * Redirects and collects system output streams.
		 */
		@Rule
		public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

		protected final TinylogLoggingProvider provider;

		/** */
		private AbstractTest() {
			this.provider = new TinylogLoggingProvider();

		}

		/**
		 * Clears global tinylog configuration.
		 */
		@AfterClass
		public static void clear() {
			Configuration.replace(emptyMap());
		}

	}

}
