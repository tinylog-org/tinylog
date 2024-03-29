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

package org.tinylog.slf4j;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link LegacyTinylogLoggerFactory}.
 */
public final class LegacyTinylogLoggerFactoryTest {

	/**
	 * Mocks the underlying logging provider.
	 */
	@Before
	public void init() {
		LoggingProvider provider = mock(LoggingProvider.class);
		Whitebox.setInternalState(AbstractTinylogLogger.class, provider);
	}

	/**
	 * Resets the underlying logging provider.
	 */
	@After
	public void reset() {
		Whitebox.setInternalState(AbstractTinylogLogger.class, ProviderRegistry.getLoggingProvider());
	}
	
	/**
	 * Verifies that the same logger instance will be returned for the same name.
	 */
	@Test
	public void sameLoggers() {
		LegacyTinylogLoggerFactory factory = new LegacyTinylogLoggerFactory();

		LegacyTinylogLogger first = factory.getLogger("abc");
		assertThat(first.getName()).isEqualTo("abc");

		LegacyTinylogLogger second = factory.getLogger("abc");
		assertThat(second.getName()).isEqualTo("abc");
		assertThat(second).isSameAs(first);
	}
	
	/**
	 * Verifies that different logger instances will be returned for different names.
	 */
	@Test
	public void differentLoggers() {
		LegacyTinylogLoggerFactory factory = new LegacyTinylogLoggerFactory();

		LegacyTinylogLogger first = factory.getLogger("abc");
		assertThat(first.getName()).isEqualTo("abc");

		LegacyTinylogLogger second = factory.getLogger("ABC");
		assertThat(second.getName()).isEqualTo("ABC");
	}

}
