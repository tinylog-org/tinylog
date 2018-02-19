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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PlainTextSegment}.
 */
public final class PlainTextSegmentTest {

	/**
	 * Verifies that the passed plain static text will be returned as static text.
	 */
	@Test
	public void doesHaveStaticText() {
		PlainTextSegment segment = new PlainTextSegment("test");
		assertThat(segment.getStaticText()).isEqualTo("test");
	}

	/**
	 * Verifies that the passed plain static text will be returned as generated token.
	 */
	@Test
	public void createToken() {
		PlainTextSegment segment = new PlainTextSegment("test");
		assertThat(segment.createToken(null, null)).isEqualTo("test");
	}

	/**
	 * Verifies that the plain static text will be accepted as valid token.
	 */
	@Test
	public void validateValidToken() {
		PlainTextSegment segment = new PlainTextSegment("test");
		assertThat(segment.validateToken("test")).isTrue();
	}

	/**
	 * Verifies that other texts will be not accepted as token.
	 */
	@Test
	public void validateInvalidToken() {
		PlainTextSegment segment = new PlainTextSegment("test");
		assertThat(segment.validateToken("Hello World!")).isFalse();
	}

}
