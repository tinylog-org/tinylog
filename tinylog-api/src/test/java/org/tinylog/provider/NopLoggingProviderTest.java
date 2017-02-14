/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.provider;

import org.junit.Before;
import org.junit.Test;
import org.tinylog.Level;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NopLoggingProvider}.
 */
public final class NopLoggingProviderTest {

	private NopLoggingProvider provider;

	/**
	 * Initializes NOP logging provider.
	 */
	@Before
	public void init() {
		provider = new NopLoggingProvider();
	}

	/**
	 * Verifies that there is an associated NOP context provider.
	 */
	@Test
	public void getContextProvider() {
		assertThat(provider.getContextProvider()).isInstanceOf(NopContextProvider.class);
	}

	/**
	 * Verifies that the minimum severity level is {@link Level#OFF}.
	 */
	@Test
	public void getMinimumLevel() {
		assertThat(provider.getMinimumLevel(null)).isEqualTo(Level.OFF);
		assertThat(provider.getMinimumLevel("test")).isEqualTo(Level.OFF);
	}

	/**
	 * Verifies that all severity levels for untagged log entries are disabled.
	 */
	@Test
	public void isEnabledUntagged() {
		assertThat(provider.isEnabled(0, null, Level.TRACE)).isFalse();
		assertThat(provider.isEnabled(1, null, Level.DEBUG)).isFalse();
		assertThat(provider.isEnabled(2, null, Level.INFO)).isFalse();
		assertThat(provider.isEnabled(3, null, Level.WARNING)).isFalse();
		assertThat(provider.isEnabled(4, null, Level.ERROR)).isFalse();
	}

	/**
	 * Verifies that all severity levels for tagged log entries are disabled.
	 */
	@Test
	public void isEnabledTagged() {
		assertThat(provider.isEnabled(0, "test", Level.TRACE)).isFalse();
		assertThat(provider.isEnabled(1, "test", Level.DEBUG)).isFalse();
		assertThat(provider.isEnabled(2, "test", Level.INFO)).isFalse();
		assertThat(provider.isEnabled(3, "test", Level.WARNING)).isFalse();
		assertThat(provider.isEnabled(4, "test", Level.ERROR)).isFalse();
	}

	/**
	 * Verifies that {@code log()} method is invokable without throwing any exceptions.
	 */
	@Test
	public void log() {
		provider.log(0, null, Level.DEBUG, null, null, (Object[]) null);
		provider.log(1, null, Level.ERROR, null, null, (Object[]) null);
	}

	/**
	 * Verifies that {@code shutdown()} method is invokable without throwing any exceptions.
	 */
	@Test
	public void shutdown() {
		provider.shutdown();
	}

}
