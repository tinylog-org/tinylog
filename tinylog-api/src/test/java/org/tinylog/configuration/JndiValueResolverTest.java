/*
 * Copyright 2022 Martin Winandy
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

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link JndiValueResolver}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(InitialContext.class)
public final class JndiValueResolverTest {

	private static final JndiValueResolver resolver = JndiValueResolver.INSTANCE;

	/**
	 * Initialize mocking of {@link InitialContext}.
	 */
	@Before
	public void init() {
		mockStatic(InitialContext.class);
	}

	/**
	 * Verifies the name of the resolver.
	 */
	@Test
	public void name() {
		assertThat(resolver.getName()).isEqualTo("JNDI values");
	}

	/**
	 * Verifies the prefix of the resolver.
	 */
	@Test
	public void prefix() {
		assertThat(resolver.getPrefix()).isEqualTo('@');
	}

	/**
	 * Verifies that the String value of a JNDI name can be resolved by providing the full JNDI name.
	 *
	 * @throws NamingException
	 *             Failed mocking JNDI lookup
	 */
	@Test
	public void resolveWithPrefix() throws NamingException {
		when(InitialContext.doLookup("java:comp/env/foo")).thenReturn("bar");
		assertThat(resolver.resolve("java:comp/env/foo")).isEqualTo("bar");
	}

	/**
	 * Verifies that the String value of a JNDI name can be resolved by providing the JNDI name without the default
	 * prefix.
	 *
	 * @throws NamingException
	 *             Failed mocking JNDI lookup
	 */
	@Test
	public void resolveWithoutPrefix() throws NamingException {
		when(InitialContext.doLookup("java:comp/env/foo")).thenReturn("bar");
		assertThat(resolver.resolve("foo")).isEqualTo("bar");
	}

}
