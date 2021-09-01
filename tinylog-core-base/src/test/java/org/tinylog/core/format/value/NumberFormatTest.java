package org.tinylog.core.format.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberFormatTest {

	/**
	 * Verifies that bytes can be formatted.
	 */
	@Test
	void byteValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		byte value = 1;
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("1.00");
	}

	/**
	 * Verifies that shorts can be formatted.
	 */
	@Test
	void shortValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		short value = 1000;
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("1,000.00");
	}

	/**
	 * Verifies that integers can be formatted.
	 */
	@Test
	void integerValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		int value = 1_000_000;
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("1,000,000.00");
	}

	/**
	 * Verifies that longs can be formatted.
	 */
	@Test
	void longValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		long value = 1_000_000_000L;
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("1,000,000,000.00");
	}

	/**
	 * Verifies that a {@link BigInteger} can be formatted.
	 */
	@Test
	void bigIntegerValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		BigInteger value = BigInteger.valueOf(1_000_000_000_000L);
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("1,000,000,000,000.00");
	}

	/**
	 * Verifies that floats can be formatted.
	 */
	@Test
	void floatValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		float value = 3.14f;
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("3.14");
	}

	/**
	 * Verifies that doubles can be formatted.
	 */
	@Test
	void doubleValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		double value = Math.PI * 1_000;
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("3,141.59");
	}

	/**
	 * Verifies that a {@link BigDecimal} can be formatted.
	 */
	@Test
	void bigDecimalValue() {
		NumberFormat format = new NumberFormat(Locale.US);
		BigDecimal value = BigDecimal.valueOf(Math.PI * 1_000_000);
		assertThat(format.isSupported(value)).isTrue();
		assertThat(format.format("#,###.00", value)).isEqualTo("3,141,592.65");
	}

	/**
	 * Verifies that strings are not supported.
	 */
	@Test
	void stringValue() {
		assertThat(new NumberFormat(Locale.US).isSupported("foo")).isFalse();
	}

}
