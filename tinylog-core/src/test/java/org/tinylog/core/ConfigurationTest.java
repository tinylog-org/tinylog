package org.tinylog.core;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@CaptureLogEntries
class ConfigurationTest {

    @Inject
    private Log log;

    /**
     * Tests for value getters.
     */
    @Nested
    class Values {

        /**
         * Verifies that all present values can be received.
         */
        @Test
        void allValues() {
            Map<String, String> properties = new LinkedHashMap<>();
            properties.put("foo", "42");
            properties.put("bar", "0");

            Configuration configuration = new Configuration(properties);
            assertThat(configuration.getAllValues()).containsExactly(entry("foo", "42"), entry("bar", "0"));
        }

        /**
         * Verifies that the check for presence of an existing value returns {@code true}.
         */
        @Test
        void checkPresenceOfExistingValue() {
            Configuration configuration = new Configuration(singletonMap("foo", "42"));
            assertThat(configuration.isPresent("foo")).isTrue();
        }

        /**
         * Verifies that the check for presence of a missing value returns {@code false}.
         */
        @Test
        void checkPresenceOfMissingValue() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.isPresent("foo")).isFalse();
        }

        /**
         * Verifies that an empty value for property "locale" is interpreted as {@link Locale#ROOT}.
         */
        @Test
        void getExistingEmptyLocale() {
            Configuration configuration = new Configuration(singletonMap("locale", ""));
            assertThat(configuration.getLocale()).isEqualTo(Locale.ROOT);
        }

        /**
         * Verifies that a locale that contains only the language can be created from property "locale".
         */
        @Test
        void getExistingLocaleWithLanguageOnly() {
            Configuration configuration = new Configuration(singletonMap("locale", "de"));
            assertThat(configuration.getLocale()).isEqualTo(new Locale("de"));
        }

        /**
         * Verifies that a locale that contains language and country can be created from property "locale".
         */
        @Test
        void getExistingLocaleWithLanguageAndCountry() {
            Configuration configuration = new Configuration(singletonMap("locale", "it_CH"));
            assertThat(configuration.getLocale()).isEqualTo(new Locale("it", "CH"));
        }

        /**
         * Verifies that a locale that contains language, country, and variant can be created from property "locale".
         */
        @Test
        void getExistingFullLocale() {
            Configuration configuration = new Configuration(singletonMap("locale", "en_US_UNIX"));
            assertThat(configuration.getLocale()).isEqualTo(new Locale("en", "US", "UNIX"));
        }

        /**
         * Verifies that {@link Locale#getDefault()} will be returned if property "locale" is not set.
         */
        @Test
        void getMissingLocale() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.getLocale()).isSameAs(Locale.getDefault());
        }

        /**
         * Verifies that the locale of the parent configuration is inherited in a child configuration by default.
         */
        @Test
        void inheritLocaleFromParent() {
            Configuration parent = new Configuration(singletonMap("locale", "en"));
            Configuration child = parent.getSubConfiguration("foo");
            assertThat(child.getLocale()).isEqualTo(Locale.ENGLISH);
        }

        /**
         * Verifies that the locale of the parent configuration can be overridden by the child configuration prefix.
         */
        @Test
        void overrideLocaleFromParent() {
            Map<String, String> properties = new HashMap<>();
            properties.put("locale", "en");
            properties.put("foo.locale", "de");

            Configuration parent = new Configuration(properties);
            Configuration child = parent.getSubConfiguration("foo");
            assertThat(child.getLocale()).isEqualTo(Locale.GERMAN);
        }

        /**
         * Verifies that UTC can be set as time zone via property "zone".
         */
        @Test
        void getUtcZone() {
            Configuration configuration = new Configuration(singletonMap("zone", "UTC"));
            assertThat(configuration.getZone().normalized()).isEqualTo(ZoneOffset.UTC);
        }

        /**
         * Verifies that an offset time zone can be set relative to UTC via property "zone".
         */
        @Test
        void getOffsetZone() {
            Configuration configuration = new Configuration(singletonMap("zone", "UTC+01:30"));
            assertThat(configuration.getZone().normalized()).isEqualTo(ZoneOffset.ofHoursMinutes(1, 30));
        }

        /**
         * Verifies that Europe/London can be set as time zone via property "zone".
         */
        @Test
        void getBritishZone() {
            Configuration configuration = new Configuration(singletonMap("zone", "Europe/London"));
            assertThat(configuration.getZone()).isEqualTo(ZoneId.of("Europe/London"));
        }

        /**
         * Verifies that Europe/Berlin can be set as time zone via property "zone".
         */
        @Test
        void getGermanZone() {
            Configuration configuration = new Configuration(singletonMap("zone", "Europe/Berlin"));
            assertThat(configuration.getZone()).isEqualTo(ZoneId.of("Europe/Berlin"));
        }

        /**
         * Verifies that {@link ZoneOffset#systemDefault()} will be returned if property "zone" contains an invalid
         * value.
         */
        @Test
        void getInvalidZone() {
            Configuration configuration = new Configuration(singletonMap("zone", "Invalid/Foo"));
            assertThat(configuration.getZone()).isEqualTo(ZoneOffset.systemDefault());
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getMessage()).contains("Invalid/Foo");
            });
        }

        /**
         * Verifies that {@link ZoneOffset#systemDefault()} will be returned if property "zone" is not set.
         */
        @Test
        void getMissingZone() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.getZone()).isEqualTo(ZoneOffset.systemDefault());
        }

        /**
         * Verifies that the zone of the parent configuration is inherited in a child configuration by default.
         */
        @Test
        void inheritZoneFromParent() {
            Configuration parent = new Configuration(singletonMap("zone", "Europe/London"));
            Configuration child = parent.getSubConfiguration("foo");
            assertThat(child.getZone()).isEqualTo(ZoneId.of("Europe/London"));
        }

        /**
         * Verifies that the zone of the parent configuration can be overridden by the child configuration prefix.
         */
        @Test
        void overrideZoneFromParent() {
            Map<String, String> properties = new HashMap<>();
            properties.put("zone", "Europe/London");
            properties.put("foo.zone", "Europe/Berlin");

            Configuration parent = new Configuration(properties);
            Configuration child = parent.getSubConfiguration("foo");
            assertThat(child.getZone()).isEqualTo(ZoneId.of("Europe/Berlin"));
        }

        /**
         * Verifies that an existing value can be received.
         */
        @Test
        void getExistingStringValueWithoutDefault() {
            Configuration configuration = new Configuration(singletonMap("foo", "42"));
            assertThat(configuration.getValue("foo")).isEqualTo("42");
        }

        /**
         * Verifies that {@code null} is returned for a missing value.
         */
        @Test
        void getMissingStringValueWithoutDefault() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.getValue("foo")).isNull();
        }

        /**
         * Verifies that leading and trailing spaces of values are removed.
         */
        @Test
        void trimStringValueWithoutDefault() {
            Configuration configuration = new Configuration(singletonMap("foo", " bar "));
            assertThat(configuration.getValue("foo")).isEqualTo("bar");
        }

        /**
         * Verifies that an existing value can be received.
         */
        @Test
        void getExistingStringValueWithDefault() {
            Configuration configuration = new Configuration(singletonMap("foo", "42"));
            assertThat(configuration.getValue("foo", "-")).isEqualTo("42");
        }

        /**
         * Verifies that the passed default value is returned for a missing value.
         */
        @Test
        void getMissingStringValueWithDefault() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.getValue("foo", "-")).isEqualTo("-");
        }

        /**
         * Verifies that leading and trailing spaces of values are removed.
         */
        @Test
        void trimStringValueWithDefault() {
            Configuration configuration = new Configuration(singletonMap("foo", " bar "));
            assertThat(configuration.getValue("foo", "other")).isEqualTo("bar");
        }

        /**
         * Verifies that a single value can be returned as list.
         */
        @Test
        void getSingleListValue() {
            Configuration configuration = new Configuration(singletonMap("foo", "42"));
            assertThat(configuration.getList("foo")).containsExactly("42");
        }

        /**
         * Verifies that multiple values, which are separated by commas, can be returned as list.
         */
        @Test
        void getMultipleListValues() {
            Configuration configuration = new Configuration(singletonMap("foo", "1, 2, 3"));
            assertThat(configuration.getList("foo")).containsExactly("1", "2", "3");
        }

        /**
         * Verifies that an empty value is returned as empty list.
         */
        @Test
        void getEmptyListValue() {
            Configuration configuration = new Configuration(singletonMap("foo", ""));
            assertThat(configuration.getList("foo")).isEmpty();
        }

        /**
         * Verifies that a missing value is returned as empty list.
         */
        @Test
        void getMissingListValue() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.getList("foo")).isEmpty();
        }

    }

    /**
     * Tests for resolving sub configurations.
     */
    @Nested
    class SubConfigurations {

        /**
         * Verifies that an existing prefixed subset of the configuration can be retrieved using the default separator
         * character ".".
         */
        @Test
        void getDefaultSubConfiguration() {
            Map<String, String> properties = new LinkedHashMap<>();
            properties.put("bar", "1");
            properties.put("foo", "2");
            properties.put("foo.alice", "3");
            properties.put("foo.bob", "4");
            properties.put("foo@fred", "5");
            properties.put("foobar", "6");

            Configuration configuration = new Configuration(properties).getSubConfiguration("foo");
            assertThat(configuration.getKeys()).containsExactly("alice", "bob");
        }

        /**
         * Verifies that an existing prefixed subset of the configuration can be retrieved using a custom separator
         * character.
         */
        @Test
        void getCustomSubConfiguration() {
            Map<String, String> properties = new LinkedHashMap<>();
            properties.put("bar", "1");
            properties.put("foo", "2");
            properties.put("foo@alice", "3");
            properties.put("foo@bob", "4");
            properties.put("foo.fred", "5");
            properties.put("foobar", "6");

            Configuration configuration = new Configuration(properties).getSubConfiguration("foo", '@');
            assertThat(configuration.getKeys()).containsExactly("alice", "bob");
        }

    }

    /**
     * Tests for resolving keys.
     */
    @Nested
    class Keys {

        /**
         * Verifies that all root keys are collected completely and in order.
         */
        @Test
        void getRootKeys() {
            Map<String, String> properties = new LinkedHashMap<>();
            properties.put("bar", "1");
            properties.put("foo.alice", "2");
            properties.put("foo.bob", "3");
            properties.put("foobar", "4");
            properties.put("boo", "5");

            Configuration configuration = new Configuration(properties);
            assertThat(configuration.getRootKeys()).containsExactly("bar", "foo", "foobar", "boo");
        }

        /**
         * Verifies that all keys are collected completely and in order.
         */
        @Test
        void getKeys() {
            Map<String, String> properties = new LinkedHashMap<>();
            properties.put("bar", "1");
            properties.put("foo.alice", "2");
            properties.put("foo.bob", "3");
            properties.put("foobar", "4");
            properties.put("boo", "5");

            Configuration configuration = new Configuration(properties);
            assertThat(configuration.getKeys()).containsExactly("bar", "foo.alice", "foo.bob", "foobar", "boo");
        }

    }

    /**
     * Tests for resolving full keys.
     */
    @Nested
    class FullKey {

        /**
         * Verifies that a key is not prefixed by a root configuration.
         */
        @Test
        void rootConfiguration() {
            Configuration configuration = new Configuration(emptyMap());
            assertThat(configuration.resolveFullKey("foo")).isEqualTo("foo");
        }

        /**
         * Verifies that a key is prefixed by a child configuration.
         */
        @Test
        void childConfiguration() {
            Configuration configuration = new Configuration(emptyMap()).getSubConfiguration("bar");
            assertThat(configuration.resolveFullKey("foo")).isEqualTo("bar.foo");
        }

        /**
         * Verifies that a key is prefixed by a grandchild configuration.
         */
        @Test
        void grandchildConfiguration() {
            Configuration configuration = new Configuration(emptyMap())
                .getSubConfiguration("boo")
                .getSubConfiguration("bar");

            assertThat(configuration.resolveFullKey("foo")).isEqualTo("boo.bar.foo");
        }

    }

}
