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

package org.tinylog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.provider.LoggingProvider;
import org.tinylog.provider.ProviderRegistry;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.LevelConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.tinylog.util.LevelConfiguration.AVAILABLE_LEVELS;

/**
 * Tests for {@link TaggedLogger}.
 */
@RunWith(Parameterized.class)
@PrepareForTest(TaggedLogger.class)
public final class TaggedLoggerTest {

	private static final String TAG1 = "test";
	private static final String TAG2 = "other tag";

	/**
	 * Activates PowerMock (alternative to {@link PowerMockRunner}).
	 */
	@Rule
	public PowerMockRule rule = new PowerMockRule();

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(false);

	/**
	 * Provides information about the level for the first tag (e.g. which levels are "enabled" for the tag).
	 */
	@Parameterized.Parameter
	public LevelConfiguration tag1Configuration;

	/**
	 * Provides information about the level for the second tag (e.g. which levels are "enabled" for the tag). May be set to {@code null}
	 * when there is no second tag.
	 */
	@Parameterized.Parameter(1)
	public LevelConfiguration tag2Configuration;

	private LoggingProvider loggingProvider;
	private TaggedLogger logger;

	/**
	 * Returns all different combinations of logging levels for up two tags for the tests.
	 *
	 * @return Each object array represents a combination. A value of {@code null} means the tag isn't present in the combination.
	 */
	@Parameters(name = "{0}, {1}")
	public static Collection<Object[]> getLevels() {
		List<Object[]> levels = new ArrayList<Object[]>();

		for (LevelConfiguration first : AVAILABLE_LEVELS) {
			levels.add(new Object[] { first, null });
			for (LevelConfiguration second : AVAILABLE_LEVELS) {
				levels.add(new Object[] { first, second });
			}
		}
		return levels;
	}

	/**
	 * Mocks the underlying logging provider and creates a new tagged logger instance.
	 */
	@Before
	public void init() {
		loggingProvider = mockLoggingProvider();
		if (tag2Configuration == null) {
			logger = new TaggedLogger(TAG1);
		} else {
			logger = new TaggedLogger(Set.of(TAG1, TAG2));
		}
	}

	/**
	 * Resets the underlying logging provider.
	 *
	 * @throws Exception
	 *             Failed resetting logging provider
	 */
	@After
	public void reset() throws Exception {
		resetLoggingProvider();
	}

	/**
	 * Verifies evaluating whether {@link Level#TRACE TRACE} level is enabled.
	 */
	@Test
	public void isTraceEnabled() {
		if (tag2Configuration != null) {
			assertThat(logger.isTraceEnabled()).isEqualTo(tag1Configuration.isTraceEnabled() || tag2Configuration.isTraceEnabled());
		} else {
			assertThat(logger.isTraceEnabled()).isEqualTo(tag1Configuration.isTraceEnabled());
		}
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceObject() {
		logger.trace("Hello World!");

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.trace(supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndArguments() {
		logger.trace("Hello {}!", "World");

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.trace("The number is {}", supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceException() {
		Exception exception = new NullPointerException();

		logger.trace(exception);

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.trace(exception, "Hello World!");

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#TRACE
	 * TRACE} level.
	 */
	@Test
	public void traceExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.trace(exception, supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#TRACE TRACE}
	 * level.
	 */
	@Test
	public void traceExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.trace(exception, "Hello {}!", "World");

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#TRACE TRACE} level.
	 */
	@Test
	public void traceExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.trace(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isTraceEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.TRACE), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#DEBUG DEBUG} level is enabled.
	 */
	@Test
	public void isDebugEnabled() {
		if (tag2Configuration != null) {
			assertThat(logger.isDebugEnabled()).isEqualTo(tag1Configuration.isDebugEnabled() || tag2Configuration.isDebugEnabled());
		} else {
			assertThat(logger.isDebugEnabled()).isEqualTo(tag1Configuration.isDebugEnabled());
		}
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugObject() {
		logger.debug("Hello World!");

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.debug(supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndArguments() {
		logger.debug("Hello {}!", "World");

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.debug("The number is {}", supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugException() {
		Exception exception = new NullPointerException();

		logger.debug(exception);

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.debug(exception, "Hello World!");

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#DEBUG
	 * DEBUG} level.
	 */
	@Test
	public void debugExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.debug(exception, supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#DEBUG DEBUG}
	 * level.
	 */
	@Test
	public void debugExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.debug(exception, "Hello {}!", "World");

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#DEBUG DEBUG} level.
	 */
	@Test
	public void debugExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.debug(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isDebugEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.DEBUG), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#INFO INFO} level is enabled.
	 */
	@Test
	public void isInfoEnabled() {
		if (tag2Configuration != null) {
			assertThat(logger.isInfoEnabled()).isEqualTo(tag1Configuration.isInfoEnabled() || tag2Configuration.isInfoEnabled());
		} else {
			assertThat(logger.isInfoEnabled()).isEqualTo(tag1Configuration.isInfoEnabled());
		}
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoObject() {
		logger.info("Hello World!");

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.info(supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoMessageAndArguments() {
		logger.info("Hello {}!", "World");

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.info("The number is {}", supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoException() {
		Exception exception = new NullPointerException();

		logger.info(exception);

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.info(exception, "Hello World!");

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#INFO
	 * INFO} level.
	 */
	@Test
	public void infoExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.info(exception, supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#INFO INFO}
	 * level.
	 */
	@Test
	public void infoExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.info(exception, "Hello {}!", "World");

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#INFO INFO} level.
	 */
	@Test
	public void infoExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.info(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isInfoEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.INFO), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#WARN WARN} level is enabled.
	 */
	@Test
	public void isWarnEnabled() {

		if (tag2Configuration != null) {
			assertThat(logger.isWarnEnabled()).isEqualTo(tag1Configuration.isWarnEnabled() || tag2Configuration.isWarnEnabled());
		} else {
			assertThat(logger.isWarnEnabled()).isEqualTo(tag1Configuration.isWarnEnabled());
		}
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnObject() {
		logger.warn("Hello World!");

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.warn(supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnMessageAndArguments() {
		logger.warn("Hello {}!", "World");

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.warn("The number is {}", supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnException() {
		Exception exception = new NullPointerException();

		logger.warn(exception);

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.warn(exception, "Hello World!");

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#WARN
	 * WARN} level.
	 */
	@Test
	public void warnExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.warn(exception, supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#WARN WARN}
	 * level.
	 */
	@Test
	public void warnExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.warn(exception, "Hello {}!", "World");

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#WARN WARN} level.
	 */
	@Test
	public void warnExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.warn(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isWarnEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.WARN), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies evaluating whether {@link Level#ERROR ERROR} level is enabled.
	 */
	@Test
	public void isErrorEnabled() {
		if (tag2Configuration != null) {
			assertThat(logger.isErrorEnabled()).isEqualTo(tag1Configuration.isErrorEnabled() || tag2Configuration.isErrorEnabled());
		} else {
			assertThat(logger.isErrorEnabled()).isEqualTo(tag1Configuration.isErrorEnabled());
		}
	}

	/**
	 * Verifies that a plain message object will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorObject() {
		logger.error("Hello World!");

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), isNull(), isNull(), eq("Hello World!"), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a lazy message supplier will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorLazyMessage() {
		Supplier<String> supplier = mockSupplier("Hello World!");
		logger.error(supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), isNull(), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndArguments() {
		logger.error("Hello {}!", "World");

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class), eq("Hello {}!"),
				eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that a formatted text message with lazy argument suppliers will be logged correctly at
	 * {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorMessageAndLazyArguments() {
		Supplier<Integer> supplier = mockSupplier(42);
		logger.error("The number is {}", supplier);
		verify(supplier, never()).get();

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), isNull(), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorException() {
		Exception exception = new NullPointerException();

		logger.error(exception);

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), same(exception), isNull(), isNull(), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom message will be logged correctly at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionWithMessage() {
		Exception exception = new NullPointerException();

		logger.error(exception, "Hello World!");

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), same(exception), isNull(), eq("Hello World!"),
				isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a custom lazy message supplier will be logged correctly at {@link Level#ERROR
	 * ERROR} level.
	 */
	@Test
	public void errorExceptionWithLazyMessage() {
		Exception exception = new NullPointerException();
		Supplier<String> supplier = mockSupplier("Hello World!");

		logger.error(exception, supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), same(exception), isNull(), same(supplier), isNull());
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message will be logged correctly at {@link Level#ERROR ERROR}
	 * level.
	 */
	@Test
	public void errorExceptionWithMessageAndArguments() {
		Exception exception = new NullPointerException();

		logger.error(exception, "Hello {}!", "World");

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
				eq("Hello {}!"), eq("World"));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Verifies that an exception with a formatted custom message and lazy argument suppliers will be logged correctly
	 * at {@link Level#ERROR ERROR} level.
	 */
	@Test
	public void errorExceptionWithMessageAndLazyArguments() {
		Exception exception = new NullPointerException();
		Supplier<Integer> supplier = mockSupplier(42);

		logger.error(exception, "The number is {}", supplier);

		verify(supplier, never()).get();

		if (tag1Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG1), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG1), any(), any(), any(), any(), any());
		}

		if (tag2Configuration != null && tag2Configuration.isErrorEnabled()) {
			verify(loggingProvider).log(eq(2), eq(TAG2), eq(Level.ERROR), same(exception), any(AdvancedMessageFormatter.class),
				eq("The number is {}"), same(supplier));
		} else {
			verify(loggingProvider, never()).log(anyInt(), eq(TAG2), any(), any(), any(), any(), any());
		}
	}

	/**
	 * Mocks the logging provider for {@link TaggedLogger} and overrides all depending fields.
	 *
	 * @return Mock instance for logging provider
	 */
	private LoggingProvider mockLoggingProvider() {
		LoggingProvider provider = mock(LoggingProvider.class);

		when(provider.getMinimumLevel(TAG1)).thenReturn(tag1Configuration.getLevel());
		when(provider.isEnabled(anyInt(), eq(TAG1), eq(Level.TRACE))).thenReturn(tag1Configuration.isTraceEnabled());
		when(provider.isEnabled(anyInt(), eq(TAG1), eq(Level.DEBUG))).thenReturn(tag1Configuration.isDebugEnabled());
		when(provider.isEnabled(anyInt(), eq(TAG1), eq(Level.INFO))).thenReturn(tag1Configuration.isInfoEnabled());
		when(provider.isEnabled(anyInt(), eq(TAG1), eq(Level.WARN))).thenReturn(tag1Configuration.isWarnEnabled());
		when(provider.isEnabled(anyInt(), eq(TAG1), eq(Level.ERROR))).thenReturn(tag1Configuration.isErrorEnabled());

		if (tag2Configuration != null) {
			when(provider.getMinimumLevel(TAG2)).thenReturn(tag2Configuration.getLevel());
			when(provider.isEnabled(anyInt(), eq(TAG2), eq(Level.TRACE))).thenReturn(tag2Configuration.isTraceEnabled());
			when(provider.isEnabled(anyInt(), eq(TAG2), eq(Level.DEBUG))).thenReturn(tag2Configuration.isDebugEnabled());
			when(provider.isEnabled(anyInt(), eq(TAG2), eq(Level.INFO))).thenReturn(tag2Configuration.isInfoEnabled());
			when(provider.isEnabled(anyInt(), eq(TAG2), eq(Level.WARN))).thenReturn(tag2Configuration.isWarnEnabled());
			when(provider.isEnabled(anyInt(), eq(TAG2), eq(Level.ERROR))).thenReturn(tag2Configuration.isErrorEnabled());
		}

		Whitebox.setInternalState(TaggedLogger.class, provider);

		return provider;
	}

	/**
	 * Creates a mocked supplier that returns the given value.
	 *
	 * @param value
	 *            Value that should be returned by the created supplier
	 * @param <T>
	 *            Type of value
	 * @return A new supplier
	 */
	@SuppressWarnings("unchecked")
	private <T> Supplier<T> mockSupplier(final T value) {
		Supplier<T> supplier = mock(Supplier.class);
		when(supplier.get()).thenReturn(value);
		return supplier;
	}

	/**
	 * Resets the logging provider in {@link TaggedLogger}.
	 */
	private void resetLoggingProvider() {
		Whitebox.setInternalState(TaggedLogger.class, ProviderRegistry.getLoggingProvider());
	}

}
