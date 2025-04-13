package org.tinylog.core;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@Tinylog
class ConfigurationTest {

    @Inject
    private Configuration configuration;

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
        @Tinylog(configuration = {"foo=42", "bar=0"})
        @Test
        void allValues() {
            assertThat(configuration.getAllValues()).containsExactly(
                entry("foo", "42"),
                entry("bar", "0")
            );
        }

        /**
         * Verifies that an empty value for property "locale" is interpreted as {@link Locale#ROOT}.
         */
        @Tinylog(configuration = "locale=")
        @Test
        void getExistingEmptyLocale() {
            assertThat(configuration.getLocale()).isEqualTo(Locale.ROOT);
        }

        /**
         * Verifies that a locale that contains only the language can be created from property "locale".
         */
        @Tinylog(configuration = "locale=de")
        @Test
        void getExistingLocaleWithLanguageOnly() {
            assertThat(configuration.getLocale()).isEqualTo(new Locale("de"));
        }

        /**
         * Verifies that a locale that contains language and country can be created from property "locale".
         */
        @Tinylog(configuration = "locale=it_CH")
        @Test
        void getExistingLocaleWithLanguageAndCountry() {
            assertThat(configuration.getLocale()).isEqualTo(new Locale("it", "CH"));
        }

        /**
         * Verifies that a locale that contains language, country, and variant can be created from property "locale".
         */
        @Tinylog(configuration = "locale=en_US_UNIX")
        @Test
        void getExistingFullLocale() {
            assertThat(configuration.getLocale()).isEqualTo(new Locale("en", "US", "UNIX"));
        }

        /**
         * Verifies that {@link Locale#getDefault()} will be returned if property "locale" is not set.
         */
        @Test
        void getMissingLocale() {
            assertThat(configuration.getLocale()).isSameAs(Locale.getDefault());
        }

        /**
         * Verifies that the locale of the parent configuration is inherited in a child configuration by default.
         */
        @Tinylog(configuration = "locale=fr")
        @Test
        void inheritLocaleFromParent() {
            Configuration child = configuration.getSubConfiguration("foo");
            assertThat(child.getLocale()).isEqualTo(Locale.FRENCH);
        }

        /**
         * Verifies that the locale of the parent configuration can be overridden by the child configuration prefix.
         */
        @Tinylog(configuration = {"locale=en", "foo.locale=de"})
        @Test
        void overrideLocaleFromParent() {
            Configuration child = configuration.getSubConfiguration("foo");
            assertThat(child.getLocale()).isEqualTo(Locale.GERMAN);
        }

        /**
         * Verifies that UTC can be set as time zone via property "zone".
         */
        @Tinylog(configuration = "zone=UTC")
        @Test
        void getUtcZone() {
            assertThat(configuration.getZone().normalized()).isEqualTo(ZoneOffset.UTC);
        }

        /**
         * Verifies that an offset time zone can be set relative to UTC via property "zone".
         */
        @Tinylog(configuration = "zone=UTC+01:30")
        @Test
        void getOffsetZone() {
            assertThat(configuration.getZone().normalized()).isEqualTo(ZoneOffset.ofHoursMinutes(1, 30));
        }

        /**
         * Verifies that Europe/London can be set as time zone via property "zone".
         */
        @Tinylog(configuration = "zone=Europe/London")
        @Test
        void getBritishZone() {
            assertThat(configuration.getZone()).isEqualTo(ZoneId.of("Europe/London"));
        }

        /**
         * Verifies that Europe/Berlin can be set as time zone via property "zone".
         */
        @Tinylog(configuration = "zone=Europe/Berlin")
        @Test
        void getGermanZone() {
            assertThat(configuration.getZone()).isEqualTo(ZoneId.of("Europe/Berlin"));
        }

        /**
         * Verifies that {@link ZoneOffset#systemDefault()} will be returned if property "zone" contains an invalid
         * value.
         */
        @Tinylog(configuration = "zone=Invalid/Foo")
        @Test
        void getInvalidZone() {
            assertThat(configuration.getZone()).isEqualTo(ZoneOffset.systemDefault());
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getFormattedMessage(configuration)).contains("Invalid/Foo");
            });
        }

        /**
         * Verifies that {@link ZoneOffset#systemDefault()} will be returned if property "zone" is not set.
         */
        @Test
        void getMissingZone() {
            assertThat(configuration.getZone()).isEqualTo(ZoneOffset.systemDefault());
        }

        /**
         * Verifies that the zone of the parent configuration is inherited in a child configuration by default.
         */
        @Tinylog(configuration = "zone=Europe/London")
        @Test
        void inheritZoneFromParent() {
            Configuration child = configuration.getSubConfiguration("foo");
            assertThat(child.getZone()).isEqualTo(ZoneId.of("Europe/London"));
        }

        /**
         * Verifies that the zone of the parent configuration can be overridden by the child configuration prefix.
         */
        @Tinylog(configuration = {"zone=Europe/London", "foo.zone=Europe/Berlin"})
        @Test
        void overrideZoneFromParent() {
            Configuration child = configuration.getSubConfiguration("foo");
            assertThat(child.getZone()).isEqualTo(ZoneId.of("Europe/Berlin"));
        }

        /**
         * Verifies that an existing value can be received.
         */
        @Tinylog(configuration = "foo=42")
        @Test
        void getExistingStringValueWithoutDefault() {
            assertThat(configuration.getValue("foo")).isEqualTo("42");
        }

        /**
         * Verifies that {@code null} is returned for a missing value.
         */
        @Test
        void getMissingStringValueWithoutDefault() {
            assertThat(configuration.getValue("foo")).isNull();
        }

        /**
         * Verifies that leading and trailing spaces of values are removed.
         */
        @Tinylog(configuration = "foo= bar ")
        @Test
        void trimStringValueWithoutDefault() {
            assertThat(configuration.getValue("foo")).isEqualTo("bar");
        }

        /**
         * Verifies that an existing value can be received.
         */
        @Tinylog(configuration = "foo=42")
        @Test
        void getExistingStringValueWithDefault() {
            assertThat(configuration.getValue("foo", "-")).isEqualTo("42");
        }

        /**
         * Verifies that the passed default value is returned for a missing value.
         */
        @Test
        void getMissingStringValueWithDefault() {
            assertThat(configuration.getValue("foo", "-")).isEqualTo("-");
        }

        /**
         * Verifies that leading and trailing spaces of values are removed.
         */
        @Tinylog(configuration = "foo= bar ")
        @Test
        void trimStringValueWithDefault() {
            assertThat(configuration.getValue("foo", "other")).isEqualTo("bar");
        }

        /**
         * Verifies that a single value can be returned as list.
         */
        @Tinylog(configuration = "foo=42")
        @Test
        void getSingleListValue() {
            assertThat(configuration.getList("foo")).containsExactly("42");
        }

        /**
         * Verifies that multiple values, which are separated by commas, can be returned as list.
         */
        @Tinylog(configuration = "foo=1,2,3")
        @Test
        void getMultipleListValues() {
            assertThat(configuration.getList("foo")).containsExactly("1", "2", "3");
        }

        /**
         * Verifies that an empty value is returned as empty list.
         */
        @Tinylog(configuration = "foo=")
        @Test
        void getEmptyListValue() {
            assertThat(configuration.getList("foo")).isEmpty();
        }

        /**
         * Verifies that a missing value is returned as empty list.
         */
        @Test
        void getMissingListValue() {
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
        @Tinylog(configuration = {
            "bar=1",
            "foo=2",
            "foo.alice=3",
            "foo.bob=4",
            "foo@fred=5",
            "foobar=6"
        })
        @Test
        void getDefaultSubConfiguration() {
            Configuration child = configuration.getSubConfiguration("foo");
            assertThat(child.getKeys()).containsExactly("alice", "bob");
        }

        /**
         * Verifies that an existing prefixed subset of the configuration can be retrieved using a custom separator
         * character.
         */
        @Tinylog(configuration = {
            "bar=1",
            "foo=2",
            "foo@alice=3",
            "foo@bob=4",
            "foo.fred=5",
            "foobar=6"
        })
        @Test
        void getCustomSubConfiguration() {
            Configuration child = configuration.getSubConfiguration("foo", '@');
            assertThat(child.getKeys()).containsExactly("alice", "bob");
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
        @Tinylog(configuration = {
            "bar=1",
            "foo.alice=2",
            "foo.bob=3",
            "foobar=4",
            "baz=5"
        })
        @Test
        void getRootKeys() {
            assertThat(configuration.getRootKeys()).containsExactly("bar", "foo", "foobar", "baz");
        }

        /**
         * Verifies that all keys are collected completely and in order.
         */
        @Tinylog(configuration = {
            "bar=1",
            "foo.alice=2",
            "foo.bob=3",
            "foobar=4",
            "baz=5"
        })
        @Test
        void getKeys() {
            assertThat(configuration.getKeys()).containsExactly("bar", "foo.alice", "foo.bob", "foobar", "baz");
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
            assertThat(configuration.resolveFullKey("foo")).isEqualTo("foo");
        }

        /**
         * Verifies that a key is prefixed by a child configuration.
         */
        @Test
        void childConfiguration() {
            Configuration child = configuration.getSubConfiguration("bar");
            assertThat(child.resolveFullKey("foo")).isEqualTo("bar.foo");
        }

        /**
         * Verifies that a key is prefixed by a grandchild configuration.
         */
        @Test
        void grandchildConfiguration() {
            Configuration child = configuration
                .getSubConfiguration("baz")
                .getSubConfiguration("bar");
            assertThat(child.resolveFullKey("foo")).isEqualTo("baz.bar.foo");
        }

    }

}
