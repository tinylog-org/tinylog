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

import java.io.IOException;
import java.util.Collection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.tinylog.Level;
import org.tinylog.rules.SystemStreamCollector;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InternalLogger}.
 */
@RunWith(Parameterized.class)
public final class InternalLoggerTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	private Level severityLevel;
	private String outputLevel;

	/**
	 * @param severityLevel
	 *            Severity level for issuing log entry
	 * @param outputLevel
	 *            Expected severity level text in outputs
	 */
	public InternalLoggerTest(final Level severityLevel, final String outputLevel) {
		this.severityLevel = severityLevel;
		this.outputLevel = outputLevel;
	}

	/**
	 * Returns all severity levels that are relevant for testing.
	 *
	 * @return Each object array contains the severity level for issuing log entries and the expected severity level
	 *         text in outputs
	 */
	@Parameters(name = "{1}")
	public static Collection<Object[]> getLevels() {
		return asList(new Object[][] { { Level.WARNING, "WARNING" }, { Level.ERROR, "ERROR" } });
	}

	/**
	 * Verifies that plain text messages will be output correctly.
	 */
	@Test
	public void textMessage() {
		InternalLogger.log(severityLevel, "Hello World!");

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce(outputLevel)
			.containsOnlyOnce("Hello World!")
			.endsWith(System.lineSeparator())
			.hasLineCount(1);
	}

	/**
	 * Verifies that exceptions that don't provide a detail message will be output correctly.
	 */
	@Test
	public void exception() {
		InternalLogger.log(severityLevel, new NullPointerException());

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce(outputLevel)
			.containsOnlyOnce(NullPointerException.class.getName())
			.endsWith(System.lineSeparator())
			.hasLineCount(1);
	}

	/**
	 * Verifies that exceptions that provide a detail message will be output correctly.
	 */
	@Test
	public void exceptionWithDescription() {
		InternalLogger.log(severityLevel, new IOException("File not found"));

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce(outputLevel)
			.containsOnlyOnce(IOException.class.getName())
			.containsOnlyOnce("File not found")
			.endsWith(System.lineSeparator())
			.hasLineCount(1);
	}

	/**
	 * Verifies that exceptions that don't provide a detail message will be output correctly in combination with a
	 * custom text message.
	 */
	@Test
	public void exceptionAndTextMessage() {
		InternalLogger.log(severityLevel, new NullPointerException(), "Hello World!");

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce(outputLevel)
			.containsOnlyOnce(NullPointerException.class.getName())
			.containsOnlyOnce("Hello World!")
			.endsWith(System.lineSeparator())
			.hasLineCount(1);
	}

	/**
	 * Verifies that exceptions that provide a detail message will be output correctly in combination with a custom text
	 * message.
	 */
	@Test
	public void exceptionWithDescriptionAndTextMessage() {
		InternalLogger.log(severityLevel, new IOException("File not found"), "Hello World!");

		assertThat(systemStream.consumeErrorOutput())
			.containsOnlyOnce(outputLevel)
			.containsOnlyOnce(IOException.class.getName())
			.containsOnlyOnce("File not found")
			.containsOnlyOnce("Hello World!")
			.endsWith(System.lineSeparator())
			.hasLineCount(1);
	}

}
