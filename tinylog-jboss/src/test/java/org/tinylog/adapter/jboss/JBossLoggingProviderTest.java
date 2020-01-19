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

package org.tinylog.adapter.jboss;

import java.util.Locale;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tinylog.Level;
import org.tinylog.format.AdvancedMessageFormatter;
import org.tinylog.provider.ContextProvider;
import org.tinylog.util.StorageHandler;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link JBossLoggingProvider}.
 */
public final class JBossLoggingProviderTest {

	private StorageHandler handler;
	private Logger logger;

	/**
	 * Initializes loggers and storage handler.
	 */
	@Before
	public void initHandler() {
		handler = new StorageHandler();

		LogManager.getLogManager().reset();

		logger = Logger.getLogger("");
		logger.addHandler(handler);
		logger.setLevel(java.util.logging.Level.INFO);
	}

	/**
	 * Releases storage handler and resets log manager.
	 */
	@After
	public void releaseHandler() {
		handler.close();
		LogManager.getLogManager().reset();
	}

	/**
	 * Verifies that {@link JBossContextProvider} is used as context provider.
	 */
	@Test
	public void contextProvider() {
		ContextProvider contextProvider = new JBossLoggingProvider().getContextProvider();
		assertThat(contextProvider).isInstanceOf(JBossContextProvider.class);
	}

	/**
	 * Verifies that {@link Level#TRACE} is used as global minimum level.
	 */
	@Test
	public void globalMinimumLevel() {
		JBossLoggingProvider provider = new JBossLoggingProvider();
		assertThat(provider.getMinimumLevel()).isEqualTo(Level.TRACE);
	}

	/**
	 * Verifies that {@link Level#TRACE} is used as minimum level for all tags.
	 */
	@Test
	public void taggedMinimumLevel() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		assertThat(provider.getMinimumLevel(null)).isEqualTo(Level.TRACE);
		assertThat(provider.getMinimumLevel("MyTag")).isEqualTo(Level.TRACE);
	}

	/**
	 * Verifies that {@link Level#INFO}, {@link Level#WARN}, and {@link Level#ERROR} are enabled.
	 */
	@Test
	public void enabled() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		assertThat(provider.isEnabled(1, null, Level.INFO)).isTrue();
		assertThat(provider.isEnabled(1, null, Level.WARN)).isTrue();
		assertThat(provider.isEnabled(1, null, Level.ERROR)).isTrue();
	}

	/**
	 * Verifies that {@link Level#TRACE} and {@link Level#DEBUG} are disabled.
	 */
	@Test
	public void disabled() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		assertThat(provider.isEnabled(1, null, Level.TRACE)).isFalse();
		assertThat(provider.isEnabled(1, null, Level.DEBUG)).isFalse();
	}

	/**
	 * Verifies that a plain text message will be discarded if logged at debug level.
	 */
	@Test
	public void logPlainTextMessageAtDebugWithDepthOfCaller() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		Exception exception = new RuntimeException();	
		provider.log(JBossLoggingProvider.class.getName(), null, Level.DEBUG, exception, null, "Hello World", new Object[0]);
		assertThat(handler.getRecords()).isEmpty();
	}

	/**
	 * Verifies that a plain text message will be output if logged at info level.
	 */
	@Test
	public void logPlainTextMessageAtInfoWithDepthOfCaller() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		Exception exception = new RuntimeException();	
		provider.log(1, null, Level.INFO, exception, null, "Hello World", new Object[0]);
		assertThat(handler.getRecords()).hasSize(1);

		LogRecord record = handler.getRecords().get(0);
		assertThat(record.getLevel()).isEqualTo(java.util.logging.Level.INFO);
		assertThat(record.getThrown()).isEqualTo(exception);
		assertThat(record.getMessage()).isEqualTo("Hello World");
		assertThat(record.getSourceClassName()).isEqualTo(JBossLoggingProviderTest.class.getName());
	}

	/**
	 * Verifies that a parameterized message will be output if logged at info level.
	 */
	@Test
	public void logParameterizedMessageAtInfoWithDepthOfCaller() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		Exception exception = new RuntimeException();
		AdvancedMessageFormatter formatter = new AdvancedMessageFormatter(Locale.ROOT);
		provider.log(1, null, Level.INFO, exception, formatter, "Hello {}", new Object[] { "User" });
		assertThat(handler.getRecords()).hasSize(1);

		LogRecord record = handler.getRecords().get(0);
		assertThat(record.getLevel()).isEqualTo(java.util.logging.Level.INFO);
		assertThat(record.getThrown()).isEqualTo(exception);
		assertThat(record.getMessage()).isEqualTo("Hello User");
		assertThat(record.getSourceClassName()).isEqualTo(JBossLoggingProviderTest.class.getName());
	}

	/**
	 * Verifies that a plain text message will be discarded if logged at debug level.
	 */
	@Test
	public void logPlainTextMessageAtDebugWithLoggerClassName() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		Exception exception = new RuntimeException();
		provider.log(1, null, Level.DEBUG, exception, null, "Hello World", new Object[0]);
		assertThat(handler.getRecords()).isEmpty();
	}

	/**
	 * Verifies that a plain text message will be output if logged at info level.
	 */
	@Test
	public void logPlainTextMessageAtInfoWithLoggerClassName() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		Exception exception = new RuntimeException();
		provider.log(JBossLoggingProvider.class.getName(), null, Level.INFO, exception, null, "Hello World", new Object[0]);
		assertThat(handler.getRecords()).hasSize(1);

		LogRecord record = handler.getRecords().get(0);
		assertThat(record.getLevel()).isEqualTo(java.util.logging.Level.INFO);
		assertThat(record.getThrown()).isEqualTo(exception);
		assertThat(record.getMessage()).isEqualTo("Hello World");
		assertThat(record.getSourceClassName()).isEqualTo(JBossLoggingProviderTest.class.getName());
	}

	/**
	 * Verifies that a parameterized message will be output if logged at info level.
	 */
	@Test
	public void logParameterizedMessageAtInfoWithLoggerClassName() {
		JBossLoggingProvider provider = new JBossLoggingProvider();

		Exception exception = new RuntimeException();
		AdvancedMessageFormatter formatter = new AdvancedMessageFormatter(Locale.ROOT);
		provider.log(JBossLoggingProvider.class.getName(), null, Level.INFO, exception, formatter, "Hello {}", new Object[] { "User" });
		assertThat(handler.getRecords()).hasSize(1);

		LogRecord record = handler.getRecords().get(0);
		assertThat(record.getLevel()).isEqualTo(java.util.logging.Level.INFO);
		assertThat(record.getThrown()).isEqualTo(exception);
		assertThat(record.getMessage()).isEqualTo("Hello User");
		assertThat(record.getSourceClassName()).isEqualTo(JBossLoggingProviderTest.class.getName());
	}

	/**
	 * Verifies that logging provider can be shutdowned without throwing any exception.
	 */
	@Test
	public void shutdown() {
		new JBossLoggingProvider().shutdown();
	}

}
