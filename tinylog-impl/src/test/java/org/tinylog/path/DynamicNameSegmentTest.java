/*
 * Copyright 2021 Martin Winandy
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
 * Tests for {@link DynamicNameSegment}.
 */
public final class DynamicNameSegmentTest {

	/**
	 * Verifies that the passed initial dynamic name text as well as the dynamic name set via
	 * {@link DynamicNameSegment#setDynamicName(String)} will be returned as static text.
	 */
	@Test
	public void doesHaveStaticText() {
		DynamicNameSegment segment = new DynamicNameSegment("test");
		assertThat(segment.getStaticText()).isEqualTo("test");

		DynamicNameSegment.setDynamicName("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");

		DynamicNameSegment.setDynamicName("bar");
		assertThat(segment.getStaticText()).isEqualTo("bar");
	}

	/**
	 * Verifies that the passed initial dynamic name text as well as the dynamic name set via
	 * {@link DynamicNameSegment#setDynamicName(String)} will be returned as generated token.
	 */
	@Test
	public void createToken() {
		DynamicNameSegment segment = new DynamicNameSegment("test");
		assertThat(segment.createToken(null, null)).isEqualTo("test");

		DynamicNameSegment.setDynamicName("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");

		DynamicNameSegment.setDynamicName("bar");
		assertThat(segment.getStaticText()).isEqualTo("bar");
	}

	/**
	 * Verifies that the dynamic name text will be accepted as valid token.
	 */
	@Test
	public void validateValidToken() {
		DynamicNameSegment segment = new DynamicNameSegment("test");
		assertThat(segment.validateToken("test")).isTrue();
		assertThat(segment.validateToken("foo")).isFalse();

		DynamicNameSegment.setDynamicName("foo");
		assertThat(segment.validateToken("test")).isFalse();
		assertThat(segment.validateToken("foo")).isTrue();
	}

}
