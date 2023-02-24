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
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

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
import org.powermock.reflect.Whitebox;
import org.tinylog.Level;
import org.tinylog.Supplier;
import org.tinylog.configuration.Configuration;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.format.MessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.EvilWriter;
import org.tinylog.util.StorageWriter;
import org.tinylog.util.Strings;
import org.tinylog.writers.ConsoleWriter;
import org.tinylog.writers.Writer;

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

	private static final String NEW_LINE = System.lineSeparator();

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
		 * Verifies that the global minimum severity level is {@link Level#TRACE}.
		 */
		@Test
		public void globalMinimumLevel() {
			assertThat(provider.getMinimumLevel()).isEqualTo(Level.TRACE);
		}

		/**
		 * Verifies that the minimum severity level for a tag is {@link Level#TRACE}.
		 */
		@Test
		public void taggedMinimumLevel() {
			assertThat(provider.getMinimumLevel(tag)).isEqualTo(Level.TRACE);
		}

		/**
		 * Verifies that trace severity level is enabled and log entries will be output.
		 */
		@Test
		public void traceEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.TRACE)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.TRACE)).isTrue();

			provider.log(1, tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce("TRACE").containsOnlyOnce("Hello World!");

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce("TRACE").containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that debug severity level is enabled and log entries will be output.
		 */
		@Test
		public void debugEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.DEBUG)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.DEBUG)).isTrue();

			provider.log(1, tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce("DEBUG").containsOnlyOnce("Hello World!");

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce("DEBUG").containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output.
		 */
		@Test
		public void infoEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.INFO)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.INFO)).isTrue();

			provider.log(1, tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce("INFO").containsOnlyOnce("Hello World!");

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).containsOnlyOnce("INFO").containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output.
		 */
		@Test
		public void warningEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.WARN)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.WARN)).isTrue();

			provider.log(1, tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("Hello World!");

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("WARN").containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output.
		 */
		@Test
		public void errorEnabled() {
			assertThat(provider.isEnabled(1, tag, Level.ERROR)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.ERROR)).isTrue();

			provider.log(1, tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("Hello World!");

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("Hello World!");
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
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(singletonMap("level", "off"));
		}

		/**
		 * Verifies that the global minimum severity level is {@link Level#OFF}.
		 */
		@Test
		public void globalMinimumLevel() {
			assertThat(provider.getMinimumLevel()).isEqualTo(Level.OFF);
		}

		/**
		 * Verifies that the minimum severity level for a tag is {@link Level#OFF}.
		 */
		@Test
		public void taggedMinimumLevel() {
			assertThat(provider.getMinimumLevel(tag)).isEqualTo(Level.OFF);
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output.
		 */
		@Test
		public void traceDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.TRACE)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.TRACE)).isFalse();

			provider.log(1, tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that debug severity level is disabled and no log entries will be output.
		 */
		@Test
		public void debugDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.DEBUG)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.DEBUG)).isFalse();

			provider.log(1, tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that info severity level is disabled and no log entries will be output.
		 */
		@Test
		public void infoDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.INFO)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.INFO)).isFalse();

			provider.log(1, tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that warning severity level is disabled and no log entries will be output.
		 */
		@Test
		public void warningDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.WARN)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.WARN)).isFalse();

			provider.log(1, tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEmpty();
		}

		/**
		 * Verifies that error severity level is disabled and no log entries will be output.
		 */
		@Test
		public void errorDisabled() {
			assertThat(provider.isEnabled(1, tag, Level.ERROR)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), tag, Level.ERROR)).isFalse();

			provider.log(1, tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEmpty();
		}

	}

	/**
	 * Tests for custom severity level for a class (custom severity level is {@link Level#DEBUG} and global severity
	 * level is {@link Level#WARN}).
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
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(emptyMap());

			Configuration.set("writer", "console");
			Configuration.set("writer.format", "{level}: {message}");

			Configuration.set("level", "warn");
			Configuration.set("level@" + CustomSeverityLevelForClass.class.getName(), "debug");
		}

		/**
		 * Verifies that the global minimum severity level is {@link Level#DEBUG}.
		 */
		@Test
		public void globalMinimumLevel() {
			assertThat(provider.getMinimumLevel()).isEqualTo(Level.DEBUG);
		}

		/**
		 * Verifies that the minimum severity level for a tag is {@link Level#DEBUG}.
		 */
		@Test
		public void taggedMinimumLevel() {
			assertThat(provider.getMinimumLevel(tag)).isEqualTo(Level.DEBUG);
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output for inner class.
		 */
		@Test
		public void traceDisabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.TRACE)).isFalse();

			provider.log(DEPTH_INNER_CLASS, tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output for outer class.
		 */
		@Test
		public void traceDisabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.TRACE)).isFalse();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(CustomSeverityLevelForClass.class.getName(), tag, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that debug severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void debugEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.DEBUG)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.DEBUG + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.DEBUG + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that debug severity level is disabled and no log entries will be output for outer class.
		 */
		@Test
		public void debugDisabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.DEBUG)).isFalse();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(CustomSeverityLevelForClass.class.getName(), tag, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void infoEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.INFO)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that info severity level is disabled and no log entries will be output for outer class.
		 */
		@Test
		public void infoDisabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.INFO)).isFalse();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(CustomSeverityLevelForClass.class.getName(), tag, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void warningEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.WARN)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output for outer class.
		 */
		@Test
		public void warningEnabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.WARN)).isTrue();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);

			provider.log(CustomSeverityLevelForClass.class.getName(), tag, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output for inner class.
		 */
		@Test
		public void errorEnabledForInnerClass() {
			assertThat(provider.isEnabled(DEPTH_INNER_CLASS, tag, Level.ERROR)).isTrue();

			provider.log(DEPTH_INNER_CLASS, tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output for outer class.
		 */
		@Test
		public void errorEnabledForOuterClass() {
			assertThat(provider.isEnabled(DEPTH_OUTER_CLASS, tag, Level.ERROR)).isTrue();

			provider.log(DEPTH_OUTER_CLASS, tag, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);

			provider.log(CustomSeverityLevelForClass.class.getName(), tag, Level.ERROR, null, null, "Hello World!");
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
		 * Verifies that the global minimum severity level for all loggers is {@link Level#TRACE}.
		 */
		@Test
		public void globalMinimumLevel() {
			assertThat(provider.getMinimumLevel()).isEqualTo(Level.TRACE);
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
			assertThat(provider.isEnabled(provider.getClass().getName(), null, Level.TRACE)).isTrue();

			provider.log(1, null, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.TRACE + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.TRACE + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that trace severity level is disabled and no log entries will be output if tagged.
		 */
		@Test
		public void taggedTraceDisabled() {
			assertThat(provider.isEnabled(1, "test", Level.TRACE)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), "test", Level.TRACE)).isFalse();

			provider.log(1, "test", Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), "test", Level.TRACE, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that debug severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedDebugEnabled() {
			assertThat(provider.isEnabled(1, null, Level.DEBUG)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), null, Level.DEBUG)).isTrue();

			provider.log(1, null, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.DEBUG + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.DEBUG + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that debug severity level is disabled and no log entries will be output if tagged.
		 */
		@Test
		public void taggedDebugDisabled() {
			assertThat(provider.isEnabled(1, "test", Level.DEBUG)).isFalse();
			assertThat(provider.isEnabled(provider.getClass().getName(), "test", Level.DEBUG)).isFalse();

			provider.log(1, "test", Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();

			provider.log(TinylogLoggingProvider.class.getName(), "test", Level.DEBUG, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEmpty();
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedInfoEnabled() {
			assertThat(provider.isEnabled(1, null, Level.INFO)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), null, Level.INFO)).isTrue();

			provider.log(1, null, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that info severity level is enabled and log entries will be output if tagged.
		 */
		@Test
		public void taggedInfoEnabled() {
			assertThat(provider.isEnabled(1, "test", Level.INFO)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), "test", Level.INFO)).isTrue();

			provider.log(1, "test", Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), "test", Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo(Level.INFO + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedWarningEnabled() {
			assertThat(provider.isEnabled(1, null, Level.WARN)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), null, Level.WARN)).isTrue();

			provider.log(1, null, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that warning severity level is enabled and log entries will be output if tagged.
		 */
		@Test
		public void taggedWarningEnabled() {
			assertThat(provider.isEnabled(1, "test", Level.WARN)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), "test", Level.WARN)).isTrue();

			provider.log(1, "test", Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), "test", Level.WARN, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.WARN + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output if untagged.
		 */
		@Test
		public void untaggedErrorEnabled() {
			assertThat(provider.isEnabled(1, null, Level.ERROR)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), null, Level.ERROR)).isTrue();

			provider.log(1, null, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that error severity level is enabled and log entries will be output if tagged.
		 */
		@Test
		public void taggedErrorEnabled() {
			assertThat(provider.isEnabled(1, "test", Level.ERROR)).isTrue();
			assertThat(provider.isEnabled(provider.getClass().getName(), "test", Level.ERROR)).isTrue();

			provider.log(1, "test", Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), "test", Level.ERROR, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput()).isEqualTo(Level.ERROR + ": Hello World!" + NEW_LINE);
		}

	}
	
	/**
	 * Tests to obtain the writers from the provider.
	 */
	public static final class ObtainWritersFromProvider extends AbstractTest {

		/**
		 * Sets some writers.
		 */
		@BeforeClass
		public static void configure() {
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(emptyMap());

			Configuration.set("writer1", "console");
			Configuration.set("writer1.tag", "TAG1");
			Configuration.set("writer1.level", Level.DEBUG.name());
			Configuration.set("writer1.format", "{level}: {message}");

			Configuration.set("writer2", "console");
			Configuration.set("writer2.level", Level.WARN.name());
			Configuration.set("writer2.tag", "TAG2");
			Configuration.set("writer2.format", "{level}: {message}");
			
			Configuration.set("writer3", "console");
			Configuration.set("writer3.level", Level.INFO.name());
			Configuration.set("writer3.tag", "TAG3");
			Configuration.set("writer3.format", "{level}: {message}");
			
			Configuration.set("writer4", "console");
			Configuration.set("writer4.level", Level.INFO.name());
			Configuration.set("writer4.tag", "TAG3");
			Configuration.set("writer4.format", "{level}: {message}");
		}

		/**
		 * Verifies that the writers can be obtained from the provider correctly.
		 */
		@Test
		public void checkWriterRetrieval() {
			assertThat(provider.getWriters()).hasSize(4);
			assertThat(provider.getWriters("TAG1")).hasSize(1);
			assertThat(provider.getWriters("TAG2")).hasSize(1);
			assertThat(provider.getWriters("TAG3")).hasSize(2);
			assertThat(provider.getWriters("XXX")).hasSize(0);
			assertThat(provider.getWriters(null)).hasSize(0);
			
			assertThat(provider.getWriters("TAG1", Level.TRACE)).hasSize(0);
			assertThat(provider.getWriters("TAG1", Level.DEBUG)).hasSize(1);
			assertThat(provider.getWriters("TAG1", Level.ERROR)).hasSize(1);

			assertThat(provider.getWriters("TAG2", Level.INFO)).hasSize(0);
			assertThat(provider.getWriters("TAG2", Level.WARN)).hasSize(1);
			assertThat(provider.getWriters("TAG2", Level.ERROR)).hasSize(1);
			
			assertThat(provider.getWriters("TAG3", Level.DEBUG)).hasSize(0);
			assertThat(provider.getWriters("TAG3", Level.INFO)).hasSize(2);
			assertThat(provider.getWriters("TAG3", Level.ERROR)).hasSize(2);
			
			assertThat(provider.getWriters("TAG3", Level.OFF)).hasSize(0);			
			assertThat(provider.getWriters("XXX", Level.ERROR)).hasSize(0);			

			Condition<Writer> condition = new Condition<Writer>() {
				@Override
				public boolean matches(final Writer o) {
					return o.getClass() == ConsoleWriter.class;
				} };
			assertThat(provider.getWriters("TAG1")).areExactly(1, condition);
			assertThat(provider.getWriters("TAG2")).areExactly(1, condition);
			assertThat(provider.getWriters("TAG3")).areExactly(2, condition);
			assertThat(provider.getWriters()).areExactly(4, condition);
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
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(doubletonMap("writingthread", "true", "autoshutdown", "false"));
		}

		/**
		 * Shuts down the logging provider.
		 * 
		 * @throws InterruptedException
		 *             Interrupted while waiting for complete shutdown
		 */
		@After
		public void shutdown() throws InterruptedException {
			provider.shutdown();
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
			provider.log(1, null, Level.INFO, null, null, "Hello World!");
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
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(doubletonMap("writer", EvilWriter.class.getName(), "autoshutdown", "false"));
		}

		/**
		 * Verifies that a thrown exception will be reported while writing.
		 */
		@Test
		public void logging() {
			provider.log(1, null, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeErrorOutput())
					.containsOnlyOnce("ERROR")
					.containsOnlyOnce(IOException.class.getName())
					.containsOnlyOnce("Hello World!");
		}

		/**
		 * Verifies that a thrown exception will be reported while shutting down a writer.
		 * 
		 * @throws InterruptedException
		 *             Interrupted while waiting for complete shutdown
		 */
		@Test
		public void shutdown() throws InterruptedException {
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
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(singletonMap("writer", StorageWriter.class.getName()));
		}

		/**
		 * Clears global tinylog configuration.
		 */
		@Before
		@After
		public void clear() {
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(emptyMap());
		}

		/**
		 * Verifies that the date of issue is present in received log entry.
		 */
		@Test
		public void date() {
			Configuration.set("writer.values", "date");

			Instant beforeIndexLog = Instant.now();
			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			Instant afterIndexLog = Instant.now();

			assertThat(StorageWriter.consumeEntries()).hasSize(1).extracting(LogEntry::getTimestamp).allSatisfy(timestamp -> {
				assertThat(timestamp.toInstant()).isBetween(beforeIndexLog, afterIndexLog);
			});

			Instant beforeClassLog = Instant.now();
			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
			Instant afterClassLog = Instant.now();

			assertThat(StorageWriter.consumeEntries()).hasSize(1).extracting(LogEntry::getTimestamp).allSatisfy(timestamp -> {
				assertThat(timestamp.toInstant()).isBetween(beforeClassLog, afterClassLog);
			});
		}

		/**
		 * Verifies that the current thread is present in received log entry.
		 */
		@Test
		public void thread() {
			Configuration.set("writer.values", "thread");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getThread).containsOnly(Thread.currentThread());

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
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

			provider.log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getContext).containsOnly(singletonMap("test", "42"));

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getContext).containsOnly(singletonMap("test", "42"));
		}

		/**
		 * Verifies that the issuing class name is present in received log entry.
		 */
		@Test
		public void className() {
			Configuration.set("writer.values", "class");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getClassName).containsOnly(LogEntryValues.class.getName());

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getClassName).containsOnly(LogEntryValues.class.getName());
		}

		/**
		 * Verifies that the issuing method name is present in received log entry.
		 */
		@Test
		public void methodName() {
			Configuration.set("writer.values", "method");
			String method = new Throwable().getStackTrace()[0].getMethodName();

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getMethodName).containsOnly(method);

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getMethodName).containsOnly(method);
		}

		/**
		 * Verifies that the issuing source file name is present in received log entry.
		 */
		@Test
		public void fileName() {
			Configuration.set("writer.values", "file");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries())
					.extracting(LogEntry::getFileName)
					.containsOnly(TinylogLoggingProviderTest.class.getSimpleName() + ".java");

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
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

			int line = new Throwable().getStackTrace()[0].getLineNumber() + 1;
			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getLineNumber).containsOnly(line);

			line = new Throwable().getStackTrace()[0].getLineNumber() + 1;
			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getLineNumber).containsOnly(line);
		}

		/**
		 * Verifies that a tag is present in received log entry.
		 */
		@Test
		public void tag() {
			Configuration.set("writer.values", "tag");

			new TinylogLoggingProvider().log(1, "test", Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getTag).containsOnly("test");

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), "test", Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getTag).containsOnly("test");
		}

		/**
		 * Verifies that severity level is present in received log entry.
		 */
		@Test
		public void level() {
			Configuration.set("writer.values", "level");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getLevel).containsOnly(Level.INFO);

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getLevel).containsOnly(Level.INFO);
		}

		/**
		 * Verifies that logged text message is present in received log entry.
		 */
		@Test
		public void message() {
			Configuration.set("writer.values", "message");

			new TinylogLoggingProvider().log(1, null, Level.INFO, null, null, "Hello World!");
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getMessage).containsOnly("Hello World!");

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, "Hello World!");
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getMessage).containsOnly("Hello World!");
		}

		/**
		 * Verifies that logged exception is present in received log entry.
		 */
		@Test
		public void exception() {
			Configuration.set("writer.values", "exception");
			Exception exception = new NullPointerException();

			new TinylogLoggingProvider().log(1, null, Level.INFO, exception, null, null);
			assertThat(StorageWriter.consumeEntries()).extracting(LogEntry::getException).containsOnly(exception);

			new TinylogLoggingProvider().log(TinylogLoggingProvider.class.getName(), null, Level.INFO, exception, null, null);
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
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(doubletonMap("writer", "console", "writer.format", "{message}"));
		}

		/**
		 * Verifies that any kind of objects can be logged, even if they don't implement {@link CharSequence}.
		 */
		@Test
		public void object() {
			provider.log(1, null, Level.INFO, null, null, 42);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("42" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, 42);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("42" + NEW_LINE);
		}

		/**
		 * Verifies that a plain text message can be logged.
		 */
		@Test
		public void plainText() {
			provider.log(1, null, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("Hello World!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, "Hello World!");
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("Hello World!" + NEW_LINE);
		}

		/**
		 * Verifies that an exception can be logged.
		 */
		@Test
		public void exception() {
			UnsupportedOperationException exception = new UnsupportedOperationException();

			provider.log(1, null, Level.ERROR, exception, null, null);
			assertThat(systemStream.consumeErrorOutput())
				.startsWith(UnsupportedOperationException.class.getName())
				.contains(TinylogLoggingProviderTest.class.getName(), "exception")
				.hasLineCount(exception.getStackTrace().length + 1);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.ERROR, exception, null, null);
			assertThat(systemStream.consumeErrorOutput())
				.startsWith(UnsupportedOperationException.class.getName())
				.contains(TinylogLoggingProviderTest.class.getName(), "exception")
				.hasLineCount(exception.getStackTrace().length + 1);
		}

		/**
		 * Verifies that a lazy message supplier can be evaluated.
		 */
		@Test
		public void lazy() {
			Supplier<String> supplier = () -> "It is " + 42;
			provider.log(1, null, Level.INFO, null, null, supplier);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("It is 42" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, null, supplier);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("It is 42" + NEW_LINE);
		}

		/**
		 * Verifies that a text message with placeholders can be logged.
		 */
		@Test
		public void arguments() {
			MessageFormatter formatter = new AdvancedMessageFormatter(Locale.ROOT, false);
			
			provider.log(1, null, Level.INFO, null, formatter, "Hello {}!", 42);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("Hello 42!" + NEW_LINE);

			provider.log(TinylogLoggingProvider.class.getName(), null, Level.INFO, null, formatter, "Hello {}!", 42);
			assertThat(systemStream.consumeStandardOutput()).isEqualTo("Hello 42!" + NEW_LINE);
		}

	}

	/**
	 * Tests for service registration.
	 */
	public static final class ServiceRegistration {

		/**
		 * Verifies that logging provider is registered under the name "tinylog".
		 */
		@Test
		public void isRegistered() {
			LoggingProvider provider = new ServiceLoader<>(LoggingProvider.class).create("tinylog");
			assertThat(provider).isInstanceOf(TinylogLoggingProvider.class);
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

		protected TinylogLoggingProvider provider;

		/**
		 * Creates logging provider.
		 */
		@Before
		public void init() {
			this.provider = new TinylogLoggingProvider();
		}

		/**
		 * Clears global tinylog configuration.
		 */
		@AfterClass
		public static void clear() {
			Whitebox.setInternalState(Configuration.class, "frozen", false);
			Configuration.replace(emptyMap());
		}

	}

}
