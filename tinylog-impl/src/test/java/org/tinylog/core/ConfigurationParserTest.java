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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.writers.ConsoleWriter;
import org.tinylog.writers.FileWriter;
import org.tinylog.writers.Writer;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * Tests for {@link ConfigurationParser}.
 */
public final class ConfigurationParserTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Resets configuration.
	 */
	@After
	public void reset() {
		Configuration.replace(emptyMap());
	}

	/**
	 * Verifies that {@link Locale#ROOT} will be used, if there is no defined locale.
	 */
	@Test
	public void defaultLocale() {
		Locale locale = ConfigurationParser.getLocale();
		assertThat(locale).isEqualTo(Locale.ROOT);
	}

	/**
	 * Verifies that an empty locale will be handled correctly.
	 */
	@Test
	public void emptyLocale() {
		Configuration.set("locale", "");
		Locale locale = ConfigurationParser.getLocale();
		assertThat(locale).isEqualTo(Locale.ROOT);
	}

	/**
	 * Verifies that a language only locale will be parsed correctly.
	 */
	@Test
	public void languageLocale() {
		Configuration.set("locale", "en");
		Locale locale = ConfigurationParser.getLocale();
		assertThat(locale).isEqualTo(new Locale("en"));
	}

	/**
	 * Verifies that a locale with language and country will be parsed correctly.
	 */
	@Test
	public void countryLocale() {
		Configuration.set("locale", "en_US");
		Locale locale = ConfigurationParser.getLocale();
		assertThat(locale).isEqualTo(new Locale("en", "US"));
	}

	/**
	 * Verifies that a full locale with language, country and variant will be parsed correctly.
	 */
	@Test
	public void fullLocale() {
		Configuration.set("locale", "no_NO_NY");
		Locale locale = ConfigurationParser.getLocale();
		assertThat(locale).isEqualTo(new Locale("no", "NO", "NY"));
	}

	/**
	 * Verifies that {@link Level#TRACE} will be used, if there is no defined global severity level.
	 */
	@Test
	public void defaultGlobalLevel() {
		Level level = ConfigurationParser.getGlobalLevel();
		assertThat(level).isEqualTo(Level.TRACE);
	}

	/**
	 * Verifies that a defined global severity level will be parsed correctly.
	 */
	@Test
	public void definedGlobalLevel() {
		Configuration.set("level", "info");
		Level level = ConfigurationParser.getGlobalLevel();
		assertThat(level).isEqualTo(Level.INFO);
	}

	/**
	 * Verifies that an invalid global severity level will be detected and {@link Level#TRACE} returned.
	 */
	@Test
	public void illegalGlobalLevel() {
		Configuration.set("level", "test");
		Level level = ConfigurationParser.getGlobalLevel();
		assertThat(level).isEqualTo(Level.TRACE);

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce("ERROR").containsOnlyOnce("severity level").containsOnlyOnce("test");
	}

	/**
	 * Verifies that no severity levels will be returned for an empty configuration.
	 */
	@Test
	public void noCustomLevels() {
		Map<String, Level> levels = ConfigurationParser.getCustomLevels();
		assertThat(levels).isEmpty();
	}

	/**
	 * Verifies that a single severity level will be parsed correctly.
	 */
	@Test
	public void singleCustomLevel() {
		Configuration.set("level@com.test", "info");

		Map<String, Level> levels = ConfigurationParser.getCustomLevels();
		assertThat(levels).containsOnly(entry("com.test", Level.INFO));
	}

	/**
	 * Verifies that multiple severity level will be found and parsed correctly.
	 */
	@Test
	public void multipleCustomLevels() {
		Configuration.set("level@test", "debug");
		Configuration.set("level@test.a", "info");
		Configuration.set("level@test.b", "warning");
		Configuration.set("level@other", "error");

		Map<String, Level> levels = ConfigurationParser.getCustomLevels();
		assertThat(levels).containsOnly(
			entry("test", Level.DEBUG),
			entry("test.a", Level.INFO),
			entry("test.b", Level.WARNING),
			entry("other", Level.ERROR));
	}

	/**
	 * Verifies that an invalid custom severity level will be ignored and an error message will be output.
	 */
	@Test
	public void illegalCustomLevel() {
		Configuration.set("level@test", "42");
		Configuration.set("level@other", "info");

		Map<String, Level> levels = ConfigurationParser.getCustomLevels();
		assertThat(levels).containsOnly(entry("other", Level.INFO));

		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("severity level").containsOnlyOnce("42");
	}

	/**
	 * Verifies that no tags will be returned for a writer without tags.
	 */
	@Test
	public void writerWithoutTag() {
		Configuration.set("writer", "console");

		List<String> tags = ConfigurationParser.getTags();
		assertThat(tags).isEmpty();
	}

	/**
	 * Verifies that a tag from a writer will be found.
	 */
	@Test
	public void writerWithTag() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "system");

		List<String> tags = ConfigurationParser.getTags();
		assertThat(tags).containsExactly("system");
	}

	/**
	 * Verifies that an empty tag from a writer will be ignored.
	 */
	@Test
	public void writerWithEmptyTag() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "");

		List<String> tags = ConfigurationParser.getTags();
		assertThat(tags).isEmpty();
	}

	/**
	 * Verifies that a minus tag from a writer will be ignored.
	 */
	@Test
	public void writerWithMinusTag() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "-");

		List<String> tags = ConfigurationParser.getTags();
		assertThat(tags).isEmpty();
	}

	/**
	 * Verifies that tags can be read from multiple writers and each tag will be returned only once.
	 */
	@Test
	public void multipleWritersWithTags() {
		Configuration.set("writer1", "console");
		Configuration.set("writer1.tag", "system");
		Configuration.set("writer2", "console");
		Configuration.set("writer2.tag", "technical");
		Configuration.set("writer3", "console");
		Configuration.set("writer3.tag", "system");

		List<String> tags = ConfigurationParser.getTags();
		assertThat(tags).containsExactlyInAnyOrder("system", "technical");
	}

	/**
	 * Verifies that writing thread is disabled by default.
	 */
	@Test
	public void noConfiguredWritingThread() {
		boolean enabled = ConfigurationParser.isWritingThreadEnabled();
		assertThat(enabled).isFalse();
	}

	/**
	 * Verifies that disabling of writing thread will be detected.
	 */
	@Test
	public void disabledWritingThread() {
		Configuration.set("writingthread", "false");

		boolean enabled = ConfigurationParser.isWritingThreadEnabled();
		assertThat(enabled).isFalse();
	}

	/**
	 * Verifies that enabling of writing thread will be detected.
	 */
	@Test
	public void enabledWritingThread() {
		Configuration.set("writingthread", "true");

		boolean enabled = ConfigurationParser.isWritingThreadEnabled();
		assertThat(enabled).isTrue();
	}

	/**
	 * Verifies that auto shutdown is disabled by default.
	 */
	@Test
	public void noConfiguredAutoShutdown() {
		boolean enabled = ConfigurationParser.isAutoShutdownEnabled();
		assertThat(enabled).isTrue();
	}

	/**
	 * Verifies that disabling of auto shutdown will be detected.
	 */
	@Test
	public void disabledAutoShutdown() {
		Configuration.set("autoshutdown", "false");

		boolean enabled = ConfigurationParser.isAutoShutdownEnabled();
		assertThat(enabled).isFalse();
	}

	/**
	 * Verifies that enabling of auto shutdown will be detected.
	 */
	@Test
	public void enabledAutoShutdown() {
		Configuration.set("autoshutdown", "true");

		boolean enabled = ConfigurationParser.isAutoShutdownEnabled();
		assertThat(enabled).isTrue();
	}

	/**
	 * Verifies that a default writer will be created for all tags and severity levels, if logging is enabled but no
	 * writer explicitly defined.
	 */
	@Test
	public void defaultWriter() {
		Collection<Writer>[][] writers = ConfigurationParser.createWriters(emptyList(), Level.TRACE, false);

		assertThat(writers).hasSize(2).allSatisfy(element ->
			assertThat(element).hasSize(5).allSatisfy(collection -> {
				assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isNotNull());
			})
		);
	}

	/**
	 * Verifies that no writer will be created, if logging is disabled.
	 */
	@Test
	public void noWriters() {
		Configuration.set("writer", "console");

		Collection<Writer>[][] writers = ConfigurationParser.createWriters(emptyList(), Level.OFF, false);

		assertThat(writers).hasSize(2).allSatisfy(element ->
			assertThat(element).hasSize(5).allSatisfy(Collection::isEmpty)
		);
	}

	/**
	 * Verifies that a single tagged writer will be created and assigned correctly.
	 */
	@Test
	public void singleTaggedWriter() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "SYSTEM");

		Collection<Writer>[][] writers = ConfigurationParser.createWriters(singletonList("SYSTEM"), Level.TRACE, false);

		assertThat(writers)
			.hasSize(3)
			.allSatisfy(element -> assertThat(element).hasSize(5));

		assertThat(writers[0]).allSatisfy(collection -> assertThat(collection).isEmpty());
		assertThat(writers[1]).allSatisfy(collection -> {
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
		assertThat(writers[2]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}

	/**
	 * Verifies that a single explicitly untagged writer will be created and assigned correctly.
	 */
	@Test
	public void singleUntaggedWriter() {
		Configuration.set("writer", "console");
		Configuration.set("writer.tag", "-");

		Collection<Writer>[][] writers = ConfigurationParser.createWriters(emptyList(), Level.TRACE, false);

		assertThat(writers).hasSize(2);
		assertThat(writers[0]).allSatisfy(collection -> {
			assertThat(collection).hasSize(1).allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
		assertThat(writers[1]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}

	/**
	 * Verifies that two tagged writers will be created and assigned correctly.
	 *
	 * @throws IOException
	 *             Failed to create temporary log file
	 */
	@Test
	public void multipleTaggedWriter() throws IOException {
		Configuration.set("writer1", "console");
		Configuration.set("writer1.tag", "SYSTEM");

		Configuration.set("writer2", "file");
		Configuration.set("writer2.tag", "SYSTEM");
		Configuration.set("writer2.file", File.createTempFile("tinylog", ".log").getAbsolutePath());

		Collection<Writer>[][] writers = ConfigurationParser.createWriters(singletonList("SYSTEM"), Level.TRACE, false);

		assertThat(writers)
			.hasSize(3)
			.allSatisfy(element -> assertThat(element).hasSize(5));

		assertThat(writers[0]).allSatisfy(collection -> assertThat(collection).isEmpty());
		assertThat(writers[1]).allSatisfy(collection -> {
			assertThat(collection)
				.hasSize(2)
				.hasAtLeastOneElementOfType(ConsoleWriter.class)
				.hasAtLeastOneElementOfType(FileWriter.class);
		});
		assertThat(writers[2]).allSatisfy(collection -> assertThat(collection).isEmpty());
	}

	/**
	 * Verifies that a writer with a defined severity level, which is above the minimum severity level, will be only
	 * assigned to the configured severity levels.
	 */
	@Test
	public void levelOfWriterAboveMinimumLevel() {
		Configuration.set("writer", "console");
		Configuration.set("writer.level", "WARNING");

		Collection<Writer>[][] writers = ConfigurationParser.createWriters(emptyList(), Level.TRACE, false);

		assertThat(writers).hasSize(2).allSatisfy(element -> {
			assertThat(element).hasSize(5);
			assertThat(element[Level.TRACE.ordinal()]).isEmpty();
			assertThat(element[Level.DEBUG.ordinal()]).isEmpty();
			assertThat(element[Level.INFO.ordinal()]).isEmpty();
			assertThat(element[Level.WARNING.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
			assertThat(element[Level.ERROR.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
	}

	/**
	 * Verifies that a writer with a defined severity level, which is below the minimum severity level, will be not
	 * assigned to severity levels below the minimum severity level.
	 */
	@Test
	public void levelOfWriterBelowMinimumLevel() {
		Configuration.set("writer", "console");
		Configuration.set("writer.level", "trace");

		Collection<Writer>[][] writers = ConfigurationParser.createWriters(emptyList(), Level.INFO, false);

		assertThat(writers).hasSize(2).allSatisfy(element -> {
			assertThat(element).hasSize(5);
			assertThat(element[Level.TRACE.ordinal()]).isEmpty();
			assertThat(element[Level.DEBUG.ordinal()]).isEmpty();
			assertThat(element[Level.INFO.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
			assertThat(element[Level.WARNING.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
			assertThat(element[Level.ERROR.ordinal()])
				.hasSize(1)
				.allSatisfy(writer -> assertThat(writer).isInstanceOf(ConsoleWriter.class));
		});
	}

}
