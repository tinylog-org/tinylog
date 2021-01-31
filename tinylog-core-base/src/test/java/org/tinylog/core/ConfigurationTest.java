package org.tinylog.core;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.loader.ConfigurationLoader;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;
import org.tinylog.core.test.service.RegisterService;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@CaptureLogEntries
class ConfigurationTest {

	@Inject
	private Framework framework;

	@Inject
	private Log log;

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
		 * Verifies that UTC can be set as time zone via property "zone".
		 */
		@Test
		void getUtcZone() {
			Configuration configuration = new Configuration().set("zone", "UTC");
			assertThat(configuration.getZone().normalized()).isEqualTo(ZoneOffset.UTC);
		}

		/**
		 * Verifies that an offset time zone can be set relative to UTC via property "zone".
		 */
		@Test
		void getOffsetZone() {
			Configuration configuration = new Configuration().set("zone", "UTC+01:30");
			assertThat(configuration.getZone().normalized()).isEqualTo(ZoneOffset.ofHoursMinutes(1, 30));
		}

		/**
		 * Verifies that Europe/London can be set as time zone via property "zone".
		 */
		@Test
		void getBritishZone() {
			Configuration configuration = new Configuration().set("zone", "Europe/London");
			assertThat(configuration.getZone()).isEqualTo(ZoneId.of("Europe/London"));
		}

		/**
		 * Verifies that Europe/Berlin can be set as time zone via property "zone".
		 */
		@Test
		void getGermanZone() {
			Configuration configuration = new Configuration().set("zone", "Europe/Berlin");
			assertThat(configuration.getZone()).isEqualTo(ZoneId.of("Europe/Berlin"));
		}

		/**
		 * Verifies that {@link ZoneOffset#systemDefault()} will be returned if property "zone" contains an invalid
		 * value.
		 */
		@Test
		void getInvalidZone() {
			Configuration configuration = new Configuration().set("zone", "Invalid/Foo");
			assertThat(configuration.getZone()).isEqualTo(ZoneOffset.systemDefault());
			assertThat(log.consume()).anySatisfy(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
				assertThat(entry.getMessage()).contains("Invalid/Foo");
			});
		}

		/**
		 * Verifies that {@link ZoneOffset#systemDefault()} will be returned if property "zone" is not set.
		 */
		@Test
		void getMissingZone() {
			Configuration configuration = new Configuration();
			assertThat(configuration.getZone()).isEqualTo(ZoneOffset.systemDefault());
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
			service = ConfigurationLoader.class,
			implementations = {TestOneConfigurationLoader.class, TestTwoConfigurationLoader.class}
		)
		@Test
		void useConfigurationLoaderWithHighestPriority() {
			TestOneConfigurationLoader.data = singletonMap("first", "yes");
			TestTwoConfigurationLoader.data = singletonMap("second", "yes");

			Configuration configuration = new Configuration();
			configuration.load(framework);

			assertThat(configuration.getValue("first")).isNull();
			assertThat(configuration.getValue("second")).isEqualTo("yes");
		}

		/**
		 * Verifies that a configuration loader that cannot provide and configuration is skipped.
		 */
		@RegisterService(
			service = ConfigurationLoader.class,
			implementations = {TestOneConfigurationLoader.class, TestTwoConfigurationLoader.class}
		)
		@Test
		void skipConfigurationLoaderWithoutResult() {
			TestOneConfigurationLoader.data = singletonMap("first", "yes");
			TestTwoConfigurationLoader.data = null;

			Configuration configuration = new Configuration();
			configuration.load(framework);

			assertThat(configuration.getValue("first")).isEqualTo("yes");
			assertThat(configuration.getValue("second")).isNull();
		}

		/**
		 * Verifies that a configuration loader can be defined by name.
		 */
		@RegisterService(
			service = ConfigurationLoader.class,
			implementations = {TestOneConfigurationLoader.class, TestTwoConfigurationLoader.class}
		)
		@Test
		void defineConfigurationLoaderByName() throws Exception {
			TestOneConfigurationLoader.data = singletonMap("first", "yes");
			TestTwoConfigurationLoader.data = singletonMap("second", "yes");

			Configuration configuration = new Configuration();

			restoreSystemProperties(() -> {
				System.setProperty("tinylog.configurationLoader", "test1");
				configuration.load(framework);
			});

			assertThat(configuration.getValue("first")).isEqualTo("yes");
			assertThat(configuration.getValue("second")).isNull();
		}

		/**
		 * Verifies that an invalid configuration name is reported and another available configuration loader will be
		 * used instead.
		 */
		@RegisterService(
			service = ConfigurationLoader.class,
			implementations = TestOneConfigurationLoader.class
		)
		@Test
		void reportInvalidConfigurationLoaderName() throws Exception {
			TestOneConfigurationLoader.data = singletonMap("foo", "bar");
			Configuration configuration = new Configuration();

			restoreSystemProperties(() -> {
				System.setProperty("tinylog.configurationLoader", "test0");
				configuration.load(framework);
			});

			assertThat(configuration.getValue("foo")).isEqualTo("bar");
			assertThat(log.consume())
				.hasSize(1)
				.allSatisfy(entry -> {
					assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
					assertThat(entry.getMessage()).contains("test0");
				});
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
	public static final class TestOneConfigurationLoader implements ConfigurationLoader {

		private static Map<Object, Object> data;

		@Override
		public String getName() {
			return "test1";
		}

		@Override
		public int getPriority() {
			return 1;
		}

		@Override
		public Map<Object, Object> load(Framework framework) {
			return data;
		}

	}

	/**
	 * Additional logging configuration builder for JUnit tests.
	 */
	public static final class TestTwoConfigurationLoader implements ConfigurationLoader {

		private static Map<Object, Object> data;

		@Override
		public String getName() {
			return "test2";
		}

		@Override
		public int getPriority() {
			return 2;
		}

		@Override
		public Map<Object, Object> load(Framework framework) {
			return data;
		}

	}

}
