package org.tinylog.core;

import java.util.Locale;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.loader.ConfigurationLoaderBuilder;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.service.RegisterService;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class ConfigurationTest {

	@Inject
	private Framework framework;

	/**
	 * Tests for getting and setting values.
	 */
	@Nested
	class Values {

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
			assertThat(configuration.isFrozen()).isFalse();

			configuration.freeze();
			assertThat(configuration.isFrozen()).isTrue();
			assertThatCode(() -> configuration.set("foo", "42")).isInstanceOf(UnsupportedOperationException.class);
		}

	}

	/**
	 * Tests for loading from registered configuration loaders.
	 */
	@Nested
	class Loading {

		/**
		 * Verifies that the configuration loader with the highest priority will be used.
		 */
		@RegisterService(
			service = ConfigurationLoaderBuilder.class,
			implementations = {TestOneConfigurationLoaderBuilder.class, TestTwoConfigurationLoaderBuilder.class}
		)
		@Test
		void useConfigurationLoaderWithHighestPriority() {
			ConfigurationLoader firstLoader = TestOneConfigurationLoaderBuilder.loader;
			when(firstLoader.load(any(ClassLoader.class))).thenReturn(singletonMap("first", "yes"));

			ConfigurationLoader secondLoader = TestTwoConfigurationLoaderBuilder.loader;
			when(secondLoader.load(any(ClassLoader.class))).thenReturn(singletonMap("second", "yes"));

			Configuration configuration = new Configuration();
			configuration.load(framework);

			assertThat(configuration.getValue("first")).isNull();
			assertThat(configuration.getValue("second")).isEqualTo("yes");
		}

		/**
		 * Verifies that a configuration loader that cannot provide and configuration is skipped.
		 */
		@RegisterService(
			service = ConfigurationLoaderBuilder.class,
			implementations = {TestOneConfigurationLoaderBuilder.class, TestTwoConfigurationLoaderBuilder.class}
		)
		@Test
		void skipConfigurationLoaderWithoutResult() {
			ConfigurationLoader firstLoader = TestOneConfigurationLoaderBuilder.loader;
			when(firstLoader.load(any(ClassLoader.class))).thenReturn(singletonMap("first", "yes"));

			ConfigurationLoader secondLoader = TestTwoConfigurationLoaderBuilder.loader;
			when(secondLoader.load(any(ClassLoader.class))).thenReturn(null);

			Configuration configuration = new Configuration();
			configuration.load(framework);

			assertThat(configuration.getValue("first")).isEqualTo("yes");
			assertThat(configuration.getValue("second")).isNull();
		}

		/**
		 * Verifies that loading is not allowed after freezing.
		 */
		@Test
		void freeze() {
			Configuration configuration = new Configuration();
			assertThat(configuration.isFrozen()).isFalse();

			configuration.freeze();
			assertThat(configuration.isFrozen()).isTrue();
			assertThatCode(() -> configuration.load(framework)).isInstanceOf(UnsupportedOperationException.class);
		}

	}

	/**
	 * Additional logging configuration builder for JUnit tests.
	 */
	public static final class TestOneConfigurationLoaderBuilder implements ConfigurationLoaderBuilder {

		private static final ConfigurationLoader loader = mock(ConfigurationLoader.class);

		@Override
		public String getName() {
			return "test1";
		}

		@Override
		public int getPriority() {
			return 1;
		}

		@Override
		public ConfigurationLoader create() {
			return loader;
		}

	}

	/**
	 * Additional logging configuration builder for JUnit tests.
	 */
	public static final class TestTwoConfigurationLoaderBuilder implements ConfigurationLoaderBuilder {

		private static final ConfigurationLoader loader = mock(ConfigurationLoader.class);

		@Override
		public String getName() {
			return "test2";
		}

		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public ConfigurationLoader create() {
			return loader;
		}

	}

}
