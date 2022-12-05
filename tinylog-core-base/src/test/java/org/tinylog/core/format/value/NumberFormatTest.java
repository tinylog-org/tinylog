package org.tinylog.core.format.value;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.inject.Inject;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.tinylog.core.Framework;
import org.tinylog.core.test.log.CaptureLogEntries;

import static org.assertj.core.api.Assertions.assertThat;

class NumberFormatTest {

    /**
     * Tests for all known supported number types.
     */
    @CaptureLogEntries(configuration = "locale=en_US")
    @Nested
    class ValueTypes {

        @Inject
        private Framework framework;

        /**
         * Verifies that bytes can be formatted.
         */
        @Test
        void byteValue() {
            NumberFormat format = new NumberFormat();
            byte value = 1;
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("1.00");
        }

        /**
         * Verifies that shorts can be formatted.
         */
        @Test
        void shortValue() {
            NumberFormat format = new NumberFormat();
            short value = 1000;
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("1,000.00");
        }

        /**
         * Verifies that integers can be formatted.
         */
        @Test
        void integerValue() {
            NumberFormat format = new NumberFormat();
            int value = 1_000_000;
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("1,000,000.00");
        }

        /**
         * Verifies that longs can be formatted.
         */
        @Test
        void longValue() {
            NumberFormat format = new NumberFormat();
            long value = 1_000_000_000L;
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("1,000,000,000.00");
        }

        /**
         * Verifies that a {@link BigInteger} can be formatted.
         */
        @Test
        void bigIntegerValue() {
            NumberFormat format = new NumberFormat();
            BigInteger value = BigInteger.valueOf(1_000_000_000_000L);
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("1,000,000,000,000.00");
        }

        /**
         * Verifies that floats can be formatted.
         */
        @Test
        void floatValue() {
            NumberFormat format = new NumberFormat();
            float value = 3.14f;
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("3.14");
        }

        /**
         * Verifies that doubles can be formatted.
         */
        @Test
        void doubleValue() {
            NumberFormat format = new NumberFormat();
            double value = Math.PI * 1_000;
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("3,141.59");
        }

        /**
         * Verifies that a {@link BigDecimal} can be formatted.
         */
        @Test
        void bigDecimalValue() {
            NumberFormat format = new NumberFormat();
            BigDecimal value = BigDecimal.valueOf(Math.PI * 1_000_000);
            assertThat(format.isSupported(value)).isTrue();
            assertThat(format.format(framework, "#,###.00", value)).isEqualTo("3,141,592.65");
        }

        /**
         * Verifies that strings are not supported.
         */
        @Test
        void unsupportedStringValue() {
            NumberFormat format = new NumberFormat();
            assertThat(format.isSupported("foo")).isFalse();
        }

    }

    /**
     * Tests for different languages.
     */
    @Nested
    class Languages {

        @Inject
        private Framework framework;

        /**
         * Verifies that a number can be formatted in the British style.
         */
        @CaptureLogEntries(configuration = "locale=en_GB")
        @Test
        void britishFormat() {
            NumberFormat format = new NumberFormat();
            assertThat(format.format(framework, "#,###.00", 1000)).isEqualTo("1,000.00");
        }

        /**
         * Verifies that a number can be formatted in the German style.
         */
        @CaptureLogEntries(configuration = "locale=de_DE")
        @Test
        void germanFormat() {
            NumberFormat format = new NumberFormat();
            assertThat(format.format(framework, "#,###.00", 1000)).isEqualTo("1.000,00");
        }

    }

}
