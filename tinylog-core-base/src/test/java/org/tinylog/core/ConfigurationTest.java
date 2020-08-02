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

package org.tinylog.core;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ConfigurationTest {

	/**
	 * Verifies that a new value can be set.
	 */
	@Test
	void setNewValue() {
		Configuration configuration = new Configuration();
		Configuration other = configuration.set("foo", "42");

		assertThat(configuration.getValue("foo")).isEqualTo("42");
		assertThat(other).isSameAs(configuration);
	}

	/**
	 * Verifies that an existing value can be overwritten.
	 */
	@Test
	void overwriteExistingValue() {
		Configuration configuration = new Configuration();
		Configuration other = configuration.set("foo", "1").set("foo", "2");

		assertThat(configuration.getValue("foo")).isEqualTo("2");
		assertThat(other).isSameAs(configuration);
	}

	/**
	 * Verifies that an empty value for property "locale" is interpreted as {@link Locale#ROOT}.
	 */
	@Test
	void getExistingEmptyLocale() {
		Configuration configuration = new Configuration().set("locale", "");
		assertThat(configuration.getLocale()).isEqualTo(Locale.ROOT);
	}

	/**
	 * Verifies that a locale that contains only the language can be created from property "locale".
	 */
	@Test
	void getExistingLocaleWithLanguageOnly() {
		Configuration configuration = new Configuration().set("locale", "de");
		assertThat(configuration.getLocale()).isEqualTo(new Locale("de"));
	}

	/**
	 * Verifies that a locale that contains language and country can be created from property "locale".
	 */
	@Test
	void getExistingLocaleWithLanguageAndCountry() {
		Configuration configuration = new Configuration().set("locale", "it_CH");
		assertThat(configuration.getLocale()).isEqualTo(new Locale("it", "CH"));
	}

	/**
	 * Verifies that a locale that contains language, country, and variant can be created from property "locale".
	 */
	@Test
	void getExistingFullLocale() {
		Configuration configuration = new Configuration().set("locale", "en_US_UNIX");
		assertThat(configuration.getLocale()).isEqualTo(new Locale("en", "US", "UNIX"));
	}

	/**
	 * Verifies that {@link Locale#getDefault()} will be returned if property "locale" is not set.
	 */
	@Test
	void getMissingLocale() {
		Configuration configuration = new Configuration();
		assertThat(configuration.getLocale()).isSameAs(Locale.getDefault());
	}

	/**
	 * Verifies that an existing value can be received.
	 */
	@Test
	void getExistingStringValue() {
		Configuration configuration = new Configuration().set("foo", "42");
		assertThat(configuration.getValue("foo")).isEqualTo("42");
	}

	/**
	 * Verifies that {@code null} is returned for a missing value.
	 */
	@Test
	void getMissingStringValue() {
		Configuration configuration = new Configuration();
		assertThat(configuration.getValue("foo")).isNull();
	}

	/**
	 * Verifies that a single value can be returned as list.
	 */
	@Test
	void getSingleListValue() {
		Configuration configuration = new Configuration().set("foo", "42");
		assertThat(configuration.getList("foo")).containsExactly("42");
	}

	/**
	 * Verifies that multiple values, which are separated by commas, can be returned as list.
	 */
	@Test
	void getMultipleListValues() {
		Configuration configuration = new Configuration().set("foo", "1, 2, 3");
		assertThat(configuration.getList("foo")).containsExactly("1", "2", "3");
	}

	/**
	 * Verifies that an empty value is returned as empty list.
	 */
	@Test
	void getEmptyListValue() {
		Configuration configuration = new Configuration().set("foo", "");
		assertThat(configuration.getList("foo")).isEmpty();
	}

	/**
	 * Verifies that a missing value is returned as empty list.
	 */
	@Test
	void getMissingListValue() {
		Configuration configuration = new Configuration();
		assertThat(configuration.getList("foo")).isEmpty();
	}

	/**
	 * Verifies that no further modifications are allowed after freezing.
	 */
	@Test
	void freeze() {
		Configuration configuration = new Configuration();
		configuration.freeze();

		assertThatCode(() -> configuration.set("foo", "42")).isInstanceOf(UnsupportedOperationException.class);
		assertThatCode(configuration::loadPropertiesFile).isInstanceOf(UnsupportedOperationException.class);
	}

}
