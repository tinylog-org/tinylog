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

import org.junit.After;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.policies.DynamicPolicy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DynamicSegment}.
 */
public final class DynamicSegmentTest {

	/**
	 * Resets the static dynamic segment and policy fields.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(DynamicSegment.class, boolean.class, false, DynamicSegment.class);
		Whitebox.setInternalState(DynamicSegment.class, String.class, null, DynamicSegment.class);
		Whitebox.setInternalState(DynamicPolicy.class, boolean.class, false, DynamicPolicy.class);
	}

	/**
	 * Verifies that the passed default value will be used as dynamic text, if no other dynamic text is set yet.
	 */
	@Test
	public void useDefaultValueIfTextAbsence() {
		new DynamicSegment("foo");
		assertThat(DynamicSegment.getText()).isEqualTo("foo");
	}

	/**
	 * Verifies that the passed default value will not be used as dynamic text, if another dynamic text is already set.
	 */
	@Test
	public void useDefaultValueIfTextPresent() {
		DynamicSegment.setText("bar");
		new DynamicSegment("foo");
		assertThat(DynamicSegment.getText()).isEqualTo("bar");
	}

	/**
	 * Verifies that the dynamic text can be set globally without triggering a rollover event.
	 */
	@Test
	public void setTextInitially() {
		DynamicSegment.setText("foo");
		assertThat(DynamicSegment.getText()).isEqualTo("foo");
		assertThat(new DynamicPolicy(null).continueCurrentFile(new byte[0])).isTrue();
	}

	/**
	 * Verifies that the dynamic text can be overridden globally without triggering a rollover event.
	 */
	@Test
	public void overrideExistingText() {
		DynamicSegment.setText("foo");
		DynamicSegment.setText("bar");
		assertThat(DynamicSegment.getText()).isEqualTo("bar");
		assertThat(new DynamicPolicy(null).continueCurrentFile(new byte[0])).isTrue();
	}

	/**
	 * Verifies setting a new dynamic text will trigger a rollover event, if it is different from the previous dynamic
	 * text and is already in use.
	 */
	@Test
	public void triggerResetForDifferentText() {
		DynamicSegment segment = new DynamicSegment("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");
		assertThat(new DynamicPolicy(null).continueCurrentFile(new byte[0])).isTrue();

		DynamicSegment.setText("bar");
		assertThat(segment.getStaticText()).isEqualTo("bar");
		assertThat(new DynamicPolicy(null).continueCurrentFile(new byte[0])).isFalse();
	}

	/**
	 * Verifies setting the same dynamic text again will not trigger a rollover event, even if it is already in use.
	 */
	@Test
	public void preventResetForSameText() {
		DynamicSegment segment = new DynamicSegment("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");
		assertThat(new DynamicPolicy(null).continueCurrentFile(new byte[0])).isTrue();

		DynamicSegment.setText("foo");
		assertThat(segment.getStaticText()).isEqualTo("foo");
		assertThat(new DynamicPolicy(null).continueCurrentFile(new byte[0])).isTrue();
	}

	/**
	 * Verifies that the current dynamic text will be returned as generated token.
	 */
	@Test
	public void createToken() {
		DynamicSegment segment = new DynamicSegment("foo");
		assertThat(segment.createToken(null, null)).isEqualTo("foo");
	}

	/**
	 * Verifies that a dynamic segment without a dynamic text will reject tokens.
	 */
	@Test
	public void validateNonExistingToken() {
		DynamicSegment segment = new DynamicSegment(null);
		assertThat(segment.validateToken("foo")).isFalse();
	}

	/**
	 * Verifies that a dynamic segment with a dynamic text will accept the same tokens.
	 */
	@Test
	public void validateValidToken() {
		DynamicSegment segment = new DynamicSegment("foo");
		assertThat(segment.validateToken("foo")).isTrue();
	}

	/**
	 * Verifies that a dynamic segment with a dynamic text will reject different tokens.
	 */
	@Test
	public void validateInvalidToken() {
		DynamicSegment segment = new DynamicSegment("foo");
		assertThat(segment.validateToken("bar")).isFalse();
	}

}
