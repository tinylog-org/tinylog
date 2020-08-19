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
	 * Verifies that {@link BigInteger BigIntegers} can be formatted.
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
	 * Verifies that {@link BigDecimal BigDecimals} can be formatted.
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
