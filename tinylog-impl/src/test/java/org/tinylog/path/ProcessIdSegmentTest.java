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
import org.tinylog.runtime.RuntimeProvider;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ProcessIdSegment}.
 */
public final class ProcessIdSegmentTest {

	private static final String PROCESS_ID = Integer.toString(RuntimeProvider.getProcessId());

	/**
	 * Verifies that the current process ID will be returned as static text.
	 */
	@Test
	public void doesHaveStaticText() {
		ProcessIdSegment segment = new ProcessIdSegment();
		assertThat(segment.getStaticText()).isEqualTo(PROCESS_ID);
	}

	/**
	 * Verifies that the current process ID will be returned as generated token.
	 */
	@Test
	public void createToken() {
		ProcessIdSegment segment = new ProcessIdSegment();
		assertThat(segment.createToken(null, null)).isEqualTo(PROCESS_ID);
	}

	/**
	 * Verifies that the current process ID will be accepted as valid token.
	 */
	@Test
	public void validateValidToken() {
		ProcessIdSegment segment = new ProcessIdSegment();
		assertThat(segment.validateToken(PROCESS_ID)).isTrue();
	}

	/**
	 * Verifies that other numbers will be not accepted as token.
	 */
	@Test
	public void validateInvalidToken() {
		String otherId = Long.toString(RuntimeProvider.getProcessId() + 1L);
		ProcessIdSegment segment = new ProcessIdSegment();
		assertThat(segment.validateToken(otherId)).isFalse();
	}

}
