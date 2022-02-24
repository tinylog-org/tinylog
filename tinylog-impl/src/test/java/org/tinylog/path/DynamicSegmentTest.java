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
 * Tests for {@link DynamicSegment}.
 */
public final class DynamicSegmentTest {

	/**
	 * Verifies that the passed initial dynamic text as well as the dynamic text set via
	 * {@link DynamicSegment#setText(String)} will be returned as static text.
	 */
	@Test
	public void doesHaveStaticText() {
		DynamicSegment segment = new DynamicSegment("test");
		assertThat(segment.getStaticText()).isEqualTo("test");

		DynamicSegment.setText("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");

		DynamicSegment.setText("bar");
		assertThat(segment.getStaticText()).isEqualTo("bar");
	}

	/**
	 * Verifies that the passed initial dynamic text as well as the dynamic text set via
	 * {@link DynamicSegment#setText(String)} will be returned as generated token.
	 */
	@Test
	public void createToken() {
		DynamicSegment segment = new DynamicSegment("test");
		assertThat(segment.createToken(null, null)).isEqualTo("test");

		DynamicSegment.setText("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");

		DynamicSegment.setText("bar");
		assertThat(segment.getStaticText()).isEqualTo("bar");
	}

	/**
	 * Verifies that the dynamic text will be accepted as valid token.
	 */
	@Test
	public void validateValidToken() {
		DynamicSegment segment = new DynamicSegment("test");
		assertThat(segment.validateToken("test")).isTrue();
		assertThat(segment.validateToken("foo")).isFalse();

		DynamicSegment.setText("foo");
		assertThat(segment.validateToken("test")).isFalse();
		assertThat(segment.validateToken("foo")).isTrue();
	}

}
