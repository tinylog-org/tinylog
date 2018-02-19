/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.path;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DateSegment}.
 */
public final class DateSegmentTest {

	/**
	 * Verifies that there is no static text.
	 */
	@Test
	public void doesNotHaveStaticText() {
		DateSegment segment = new DateSegment("yyyy-MM-dd");
		assertThat(segment.getStaticText()).isNull();
	}

	/**
	 * Verifies that the passed date will be formatted as defined and returned as token.
	 */
	@Test
	public void createToken() {
		DateSegment segment = new DateSegment("yyyy-MM-dd");
		Date date = Date.from(LocalDate.of(1985, 6, 3).atStartOfDay(ZoneOffset.systemDefault()).toInstant());
		assertThat(segment.createToken(null, date)).isEqualTo("1985-06-03");
	}

	/**
	 * Verifies that a timestamp that matches with the defined format pattern will be accepted as valid token.
	 */
	@Test
	public void validateValidToken() {
		DateSegment segment = new DateSegment("yyyy-MM-dd");
		assertThat(segment.validateToken("1985-06-03")).isTrue();
	}

	/**
	 * Verifies that an invalid timestamp will be not accepted as token.
	 */
	@Test
	public void validateInvalidToken() {
		DateSegment segment = new DateSegment("yyyy-MM-dd");
		assertThat(segment.validateToken("1985-XX-03")).isFalse();
	}

}
