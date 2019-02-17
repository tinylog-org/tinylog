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

package org.apache.log4j;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.spi.LoggerFactory;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link LogManager}.
 */
public final class LogManagerTest {

	/**
	 * Verifies that the root logger can be received.
	 */
	@Test
	public void rootLogger() {
		Logger logger = LogManager.getRootLogger();
		assertThat(logger.getName()).isEqualTo("root");
		assertThat(logger.getParent()).isNull();
	}

	/**
	 * Verifies that a logger can be received by name.
	 */
	@Test
	public void loggerByName() {
		Logger logger = LogManager.getLogger("test.example.MyClass");
		assertThat(logger.getName()).isEqualTo("test.example.MyClass");

		Category parent = logger.getParent();
		assertThat(parent).isNotNull();
		assertThat(parent.getName()).isEqualTo("test.example");
	}

	/**
	 * Verifies that a logger can be received by name and logger factory.
	 */
	@Test
	public void loggerByNameAndFactory() {
		Logger logger = LogManager.getLogger("test.example.MyClass", mock(LoggerFactory.class));
		assertThat(logger).isSameAs(logger);
	}

	/**
	 * Verifies that a logger can be received by class.
	 */
	@Test
	public void loggerByClass() {
		Logger logger = LogManager.getLogger(LogManagerTest.class);
		assertThat(logger.getName()).isEqualTo(LogManagerTest.class.getName());

		Category parent = logger.getParent();
		assertThat(parent).isNotNull();
		assertThat(parent.getName()).isEqualTo(LogManagerTest.class.getPackageName());
	}

	/**
	 * Verifies that an existing logger can be received as soon as created.
	 */
	@Test
	public void exists() {
		Logger logger = LogManager.exists("test.example.NewClass");
		assertThat(logger).isNull();

		LogManager.getLogger("test.example.NewClass");

		logger = LogManager.exists("test.example.NewClass");
		assertThat(logger).isNotNull();
	}

	/**
	 * Verifies that all current logger instances can be received without the root logger.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void currentLoggers() {
		LogManager.getLogger("test.example.MyClass");
		LogManager.getLogger("test.example.NewClass");
		LogManager.getLogger(LogManagerTest.class);

		List<Object> loggers = Collections.list(LogManager.getCurrentLoggers());

		assertThat(loggers)
			.contains(
				LogManager.getLogger("test.example.MyClass"),
				LogManager.getLogger("test.example.NewClass"),
				LogManager.getLogger(LogManagerTest.class))
			.doesNotContain(
				LogManager.getRootLogger());
	}

	/**
	 * Verifies that the shutdown() method can be called without any side effects.
	 */
	@Test
	public void shutdown() {
		LogManager.shutdown();
	}

	/**
	 * Verifies that the resetConfiguration() method can be called without any side effects.
	 */
	@Test
	public void resetConfiguration() {
		LogManager.resetConfiguration();
	}

	/**
	 * Verifies that a parent logger can be received.
	 */
	@Test
	public void parentLogger() {
		Logger logger = LogManager.getParentLogger("test.example.MyClass");
		assertThat(logger.getName()).isEqualTo("test.example");

		Category parent = logger.getParent();
		assertThat(parent).isNotNull();
		assertThat(parent.getName()).isEqualTo("test");
	}

}
