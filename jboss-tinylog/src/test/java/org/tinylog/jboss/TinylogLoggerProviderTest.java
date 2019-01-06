/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.jboss;

import java.util.Collections;
import java.util.Map;

import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.ThreadContext;
import org.tinylog.provider.ContextProvider;
import org.tinylog.provider.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link TinylogLoggerProvider}.
 */
public final class TinylogLoggerProviderTest {

	private TinylogLoggerProvider logging;
	private ContextProvider context;

	/**
	 * Creates an instance of {@link TinylogLogger} and mocks the underlying context provider.
	 */
	@Before
	public void init() {
		logging = new TinylogLoggerProvider();
		context = mock(ContextProvider.class);
		Whitebox.setInternalState(ThreadContext.class, context);
	}

	/**
	 * Resets the underlying context provider.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(ThreadContext.class, ProviderRegistry.getLoggingProvider().getContextProvider());
	}

	/**
	 * Verifies that a logger instances of {@link TinylogLogger} will be created.
	 */
	@Test
	public void newLoggerInstance() {
		assertThat(logging.getLogger("org.test.MyClass")).isInstanceOf(TinylogLogger.class);
	}

	/**
	 * Verifies that logger instances are cached and will be reused.
	 */
	@Test
	public void cachedLoggerInstance() {
		Logger first = logging.getLogger("org.test.MyClass");
		Logger second = logging.getLogger("org.test.MyClass");
		assertThat(first).isSameAs(second);
	}

	/**
	 * Verifies that different logger instanced will be returned for different classes.
	 */
	@Test
	public void differentLoggerInstance() {
		Logger first = logging.getLogger("org.test.FirstClass");
		Logger second = logging.getLogger("org.test.SecondClass");
		assertThat(first).isNotSameAs(second);
	}

	/**
	 * Verifies that shared MDC can be cleared.
	 */
	@Test
	public void clearMdc() {
		logging.clearMdc();
		verify(context).clear();
	}

	/**
	 * Verifies that shared MDC can be updated.
	 */
	@Test
	public void putMdc() {
		logging.putMdc("test", "42");
		verify(context).put("test", "42");
	}

	/**
	 * Verifies that values from shared MDC can be fetched.
	 */
	@Test
	public void getMdc() {
		when(logging.getMdc("test")).thenReturn("42");
		assertThat(context.get("test")).isEqualTo("42");
	}

	/**
	 * Verifies that values from shared MDC can be removed.
	 */
	@Test
	public void removeMdc() {
		logging.removeMdc("test");
		verify(context).remove("test");
	}

	/**
	 * Verifies that the entire shared MDC map can be fetched.
	 */
	@Test
	public void getMdcMap() {
		Map<String, Object> map = Collections.singletonMap("test", "42");
		when(logging.getMdcMap()).thenReturn(map);
		assertThat(context.getMapping()).isEqualTo(map);
	}

	/**
	 * Verifies that NDC messages are ignored, but fulfill the specification and throw no exceptions.
	 */
	@Test
	public void ndc() {
		logging.setNdcMaxDepth(100);
		logging.pushNdc("Ignored Message");

		assertThat(logging.getNdcDepth()).isZero();
		assertThat(logging.getNdc()).isNull();
		assertThat(logging.popNdc()).isEmpty();
		assertThat(logging.peekNdc()).isEmpty();

		logging.clearNdc();
	}

}
