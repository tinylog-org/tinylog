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

import java.util.Locale;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.rules.SystemStreamCollector;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

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
			.containsOnlyOnce("ERROR")
			.containsOnlyOnce("severity level")
			.containsOnlyOnce("test");
	}

}
