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

package org.tinylog.configuration;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link EnvironmentVariableResolver}.
 */
public final class EnvironmentVariableResolverTest {

	private static final EnvironmentVariableResolver resolver = EnvironmentVariableResolver.INSTANCE;
	
	/**
	 * Verifies the name of the resolver.
	 */
	@Test
	public void name() {
		assertThat(resolver.getName()).isEqualTo("environment variables");
	}
	
	/**
	 * Verifies the prefix of the resolver.
	 */
	@Test
	public void prefix() {
		assertThat(resolver.getPrefix()).isEqualTo('$');
	}
	
	/**
	 * Verifies that an existent environment variable can be resolved.
	 */
	@Test
	public void resolveExistingVariable() {
		assertThat(resolver.resolve("PATH")).isEqualTo(System.getenv("PATH"));
	}
	
	
	/**
	 * Verifies that {@code null} will be returned for an non-existent environment variable.
	 */
	@Test
	public void resolveNonExistentVariable() {
		assertThat(resolver.resolve("1234567890_MISSING")).isNull();
	}

}
