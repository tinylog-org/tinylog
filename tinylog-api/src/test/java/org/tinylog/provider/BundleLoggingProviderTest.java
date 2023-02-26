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

import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.format.MessageFormatter;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BundleLoggingProvider}.
 */
public final class BundleLoggingProviderTest {

	private LoggingProvider first;
	private LoggingProvider second;
	private LoggingProvider bundle;

	/**
	 * Verifies that returned context provider combines all context providers from underlying logging providers.
	 */
	@Test
	public void getContextProvider() {
		init(Level.TRACE, Level.TRACE);

		ContextProvider contextProvider = bundle.getContextProvider();
		assertThat(contextProvider).isInstanceOf(BundleContextProvider.class);

		contextProvider.put("test", "42");
		verify(first.getContextProvider()).put("test", "42");
		verify(second.getContextProvider()).put("test", "42");
	}

	/**
	 * Verifies that {@code getMinimumLevel()} method returns the minimum severity level of underlying logging
	 * providers, if all have the same global minimum severity level.
	 */
	@Test
	public void getSameGlobalMinimumLevel() {
		init(Level.TRACE, Level.TRACE);
		assertThat(bundle.getMinimumLevel()).isEqualTo(Level.TRACE);
	}

	/**
	 * Verifies that {@code getMinimumLevel()} method returns the lowest minimum severity level of underlying logging
	 * providers, if there are different global minimum severity levels.
	 */
	@Test
	public void getDifferentGlobalMinimumLevel() {
		init(Level.DEBUG, Level.WARN);
		assertThat(bundle.getMinimumLevel()).isEqualTo(Level.DEBUG);
	}

	/**
	 * Verifies that {@code getMinimumLevel(String)} method returns the minimum severity level of underlying logging
	 * providers, if all have the same minimum severity level for an tag.
	 */
	@Test
	public void getSameTaggedMinimumLevel() {
		init(Level.TRACE, Level.TRACE);
		assertThat(bundle.getMinimumLevel(null)).isEqualTo(Level.TRACE);
	}

	/**
	 * Verifies that {@code getMinimumLevel(String)} method returns the lowest minimum severity level of underlying logging
	 * providers, if there are different minimum severity levels for an tag.
	 */
	@Test
	public void getDifferentTaggedMinimumLevel() {
		init(Level.DEBUG, Level.WARN);
		assertThat(bundle.getMinimumLevel(null)).isEqualTo(Level.DEBUG);
	}

	/**
	 * Verifies that {@code isEnabled()} method evaluates the severity level from underlying logging providers and
	 * returns {@code true} if given severity level is enabled at least for one of the underlying logging providers.
	 */
	@Test
	public void isEnabled() {
		init(Level.TRACE, Level.TRACE);

		when(first.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(false);
		when(first.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(false);
		when(first.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(false);
		when(first.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(true);
		when(first.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(true);

		when(second.isEnabled(anyInt(), isNull(), eq(Level.TRACE))).thenReturn(false);
		when(second.isEnabled(anyInt(), isNull(), eq(Level.DEBUG))).thenReturn(true);
		when(second.isEnabled(anyInt(), isNull(), eq(Level.INFO))).thenReturn(true);
		when(second.isEnabled(anyInt(), isNull(), eq(Level.WARN))).thenReturn(true);
		when(second.isEnabled(anyInt(), isNull(), eq(Level.ERROR))).thenReturn(true);

		assertThat(bundle.isEnabled(1, null, Level.TRACE)).isEqualTo(false);
		assertThat(bundle.isEnabled(1, null, Level.DEBUG)).isEqualTo(true);
		assertThat(bundle.isEnabled(1, null, Level.INFO)).isEqualTo(true);
		assertThat(bundle.isEnabled(1, null, Level.WARN)).isEqualTo(true);
		assertThat(bundle.isEnabled(1, null, Level.ERROR)).isEqualTo(true);

		verify(first, atLeastOnce()).isEnabled(eq(2), isNull(), any());
		verify(second, atLeastOnce()).isEnabled(eq(2), isNull(), any());
	}

	/**
	 * Verifies that {@code isEnabled()} evaluates the severity level from underlying logging providers and
	 * returns {@code true} if the given severity level is enabled at least for one of the underlying logging providers.
	 */
	@Test
	public void isEnabledWithLoggerClassName() {
		init(Level.TRACE, Level.TRACE);

		when(first.isEnabled(anyString(), isNull(), eq(Level.TRACE))).thenReturn(false);
		when(first.isEnabled(anyString(), isNull(), eq(Level.DEBUG))).thenReturn(false);
		when(first.isEnabled(anyString(), isNull(), eq(Level.INFO))).thenReturn(false);
		when(first.isEnabled(anyString(), isNull(), eq(Level.WARN))).thenReturn(true);
		when(first.isEnabled(anyString(), isNull(), eq(Level.ERROR))).thenReturn(true);

		when(second.isEnabled(anyString(), isNull(), eq(Level.TRACE))).thenReturn(false);
		when(second.isEnabled(anyString(), isNull(), eq(Level.DEBUG))).thenReturn(true);
		when(second.isEnabled(anyString(), isNull(), eq(Level.INFO))).thenReturn(true);
		when(second.isEnabled(anyString(), isNull(), eq(Level.WARN))).thenReturn(true);
		when(second.isEnabled(anyString(), isNull(), eq(Level.ERROR))).thenReturn(true);

		assertThat(bundle.isEnabled("myLoggerClassName", null, Level.TRACE)).isFalse();
		assertThat(bundle.isEnabled("myLoggerClassName", null, Level.DEBUG)).isTrue();
		assertThat(bundle.isEnabled("myLoggerClassName", null, Level.INFO)).isTrue();
		assertThat(bundle.isEnabled("myLoggerClassName", null, Level.WARN)).isTrue();
		assertThat(bundle.isEnabled("myLoggerClassName", null, Level.ERROR)).isTrue();

		verify(first, atLeastOnce()).isEnabled(eq("myLoggerClassName"), isNull(), any());
		verify(second, atLeastOnce()).isEnabled(eq("myLoggerClassName"), isNull(), any());
	}

	/**
	 * Verifies that {@link BundleLoggingProvider#log(int, String, Level, Throwable, Object, Object...)} invokes
	 * {@link LoggingProvider#log(int, String, Level, Throwable, Object, Object...)} from underlying logging providers.
	 */
	@Test
	public void logWithDepthIndex() {
		init(Level.TRACE, Level.TRACE);

		NullPointerException exception = new NullPointerException();
		MessageFormatter formatter = mock(MessageFormatter.class);
		bundle.log(1, "technical", Level.INFO, exception, formatter, "Test", 42);

		verify(first).log(2, "technical", Level.INFO, exception, formatter, "Test", 42);
		verify(second).log(2, "technical", Level.INFO, exception, formatter, "Test", 42);
	}

	/**
	 * Verifies that {@link BundleLoggingProvider#log(String, String, Level, Throwable, Object, Object...)} invokes
	 * {@link LoggingProvider#log(String, String, Level, Throwable, Object, Object...)} from underlying logging
	 * providers.
	 */
	@Test
	public void logWithLoggerClass() {
		init(Level.TRACE, Level.TRACE);

		NullPointerException exception = new NullPointerException();
		MessageFormatter formatter = mock(MessageFormatter.class);
		bundle.log(BundleContextProvider.class.getName(), "technical", Level.INFO, exception, formatter, "Test", 42);

		verify(first).log(BundleContextProvider.class.getName(), "technical", Level.INFO, exception, formatter, "Test", 42);
		verify(second).log(BundleContextProvider.class.getName(), "technical", Level.INFO, exception, formatter, "Test", 42);
	}
	
	
	/**
	 * Verifies that {@code getMinimumLevel(String)} method returns the minimum severity level of underlying logging
	 * providers, if all have the same minimum severity level for an tag.
	 */
	@Test
	public void resolveBundleLoggingProviders() {
		init(Level.TRACE, Level.TRACE);
		assertThat(((BundleLoggingProvider) bundle).getLoggingProviders())
			.hasSize(2)
			.hasOnlyElementsOfType(LoggingProvider.class);
	}


	/**
	 * Verifies that {@code shutdown()} method invokes {@code shutdown()} methods from underlying logging providers.
	 * 
	 * @throws InterruptedException
	 *             Interrupted while waiting for complete shutdown
	 */
	@Test
	public void shutdown() throws InterruptedException {
		init(Level.OFF, Level.OFF);

		bundle.shutdown();

		verify(first).shutdown();
		verify(second).shutdown();
	}

	/**
	 * Creates underlying logging providers as well as the wrapper logging provider.
	 *
	 * @param firstLevel
	 *            Minimum severity level of first underlying logging provider
	 * @param secondLevel
	 *            Minimum severity level of second underlying logging provider
	 */
	private void init(final Level firstLevel, final Level secondLevel) {
		first = mock(LoggingProvider.class);
		second = mock(LoggingProvider.class);

		when(first.getContextProvider()).thenReturn(mock(ContextProvider.class));
		when(second.getContextProvider()).thenReturn(mock(ContextProvider.class));

		when(first.getMinimumLevel()).thenReturn(firstLevel);
		when(second.getMinimumLevel()).thenReturn(secondLevel);

		when(first.getMinimumLevel(null)).thenReturn(firstLevel);
		when(second.getMinimumLevel(null)).thenReturn(secondLevel);

		bundle = new BundleLoggingProvider(asList(first, second));
	}

}
