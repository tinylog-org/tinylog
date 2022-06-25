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
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link InitialContextWrapper}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(InitialContext.class)
public class InitialContextWrapperTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Initialize mocking of {@link InitialContext}.
	 */
	@Before
	public void init() {
		mockStatic(InitialContext.class);
	}

	/**
	 * Verifies that the String value of a JNDI name can be resolved.
	 *
	 * @throws NamingException
	 *             Failed mocking JNDI lookup
	 */
	@Test
	public void resolveJndiStringValue() throws NamingException {
		when(InitialContext.doLookup("java:comp/env/foo")).thenReturn("bar");
		assertThat(InitialContextWrapper.resolve("java:comp/env/foo")).isEqualTo("bar");
	}

	/**
	 * Verifies that the {@code null} value of a JNDI name can be resolved.
	 *
	 * @throws NamingException
	 *             Failed mocking JNDI lookup
	 */
	@Test
	public void resolveJndiNullValue() throws NamingException {
		when(InitialContext.doLookup("java:comp/env/foo")).thenReturn(null);
		assertThat(InitialContextWrapper.resolve("java:comp/env/foo")).isNull();
	}

	/**
	 * Verifies that {@code null} is returned for a non-existent JNDI name.
	 *
	 * @throws NamingException
	 *             Failed mocking JNDI lookup
	 */
	@Test
	public void resolveMissingJndiName() throws NamingException {
		when(InitialContext.doLookup("java:comp/env/foo")).thenThrow(NameNotFoundException.class);
		assertThat(InitialContextWrapper.resolve("java:comp/env/foo")).isNull();
	}

	/**
	 * Verifies that {@code null} is returned and an error is logged, if the {@link InitialContext} throws any kind of
	 * {@link NamingException} during lookup.
	 *
	 * @throws NamingException
	 *             Failed mocking JNDI lookup
	 */
	@Test
	public void failResolvingInvalidJndiName() throws NamingException {
		when(InitialContext.doLookup("java:comp/env/foo")).thenThrow(InvalidNameException.class);
		assertThat(InitialContextWrapper.resolve("java:comp/env/foo")).isNull();
		assertThat(systemStream.consumeErrorOutput())
			.contains(InvalidNameException.class.getName(), "java:comp/env/foo");
	}

}
