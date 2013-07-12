/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.util.FileHelper;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.util.StoreWriter.LogEntry;
import org.pmw.tinylog.writers.RollingFileWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests old fixed bugs to prevent regressions.
 */
public class RegressionsTest extends AbstractTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Bug: Wrong class in log entry if there isn't set any special logging level for at least one package.
	 */
	@Test
	public final void testWrongClass() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.TRACE).formatPattern("{class}").activate();

		Configurator.currentConfig().level("org", LoggingLevel.TRACE).activate();
		Logger.info("");
		LogEntry logEntry = new LogEntry(LoggingLevel.INFO, RegressionsTest.class.getName() + NEW_LINE);
		assertEquals(logEntry, writer.consumeLogEntry()); // Was already OK

		Configurator.currentConfig().level("org", null).activate();
		Logger.info("");
		logEntry = new LogEntry(LoggingLevel.INFO, RegressionsTest.class.getName() + NEW_LINE);
		assertEquals(logEntry, writer.consumeLogEntry()); // Failed
	}

	/**
	 * Bug: If a log file is continued, the policy will start from scratch. This leads to a too late rollover.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		File file = FileHelper.createTemporaryFile("tmp");

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, new SizePolicy(10));
		writer.write(null, "12345");
		writer.close();

		writer = new RollingFileWriter(file.getAbsolutePath(), 0, new SizePolicy(10));
		writer.write(null, "123456");
		writer.close();

		assertEquals(6, file.length());
		file.delete();
	}

	/**
	 * Bug: IllegalArgumentException if there are curly brackets in the log message.
	 */
	@Test
	public final void testCurlyBracketsInText() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).formatPattern("{message}").activate();
		Logger.info("{TEST}");
		LogEntry logEntry = new LogEntry(LoggingLevel.INFO, "{TEST}" + NEW_LINE);
		assertEquals(logEntry, writer.consumeLogEntry()); // Failed (java.lang.IllegalArgumentException)
	}

	/**
	 * Bug: Logging writer gets active logging level instead of the logging level of the log entry.
	 */
	@Test
	public final void testLoggingLevel() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).level(LoggingLevel.INFO).formatPattern("{message}").activate();
		Logger.error("Hello");
		LogEntry logEntry = new LogEntry(LoggingLevel.ERROR, "Hello" + NEW_LINE);
		assertEquals(logEntry, writer.consumeLogEntry()); // Failed
	}

	/**
	 * Bug: If all custom logging levels for packages are lower than the default package level, the custom logging
	 * levels will be ignored.
	 */
	@Test
	public final void testLowerCustomLoggingLevelsForPackages() {
		StoreWriter writer = new StoreWriter();
		Configurator.defaultConfig().level(LoggingLevel.INFO).level(RegressionsTest.class.getPackage().getName(), LoggingLevel.OFF).activate();
		Logger.info("should be ignored"); // Was output
		assertNull(writer.consumeLogEntry());
	}
}
