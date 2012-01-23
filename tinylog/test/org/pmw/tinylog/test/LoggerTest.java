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

package org.pmw.tinylog.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.ILoggingWriter;
import org.pmw.tinylog.Logger;

/**
 * Tests for the logger.
 * 
 * @see org.pmw.tinylog.Logger
 */
public class LoggerTest {

	/**
	 * Test logging.
	 */
	@Test
	public final void testLogging() {
		LoggingWriter writer = new LoggingWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);

		Logger.info("Hello!");
		assertNotNull(writer.consumeEntry());

		Logger.error(new NullPointerException());
		assertNotNull(writer.consumeEntry());

		Logger.debug("Hello!");
		assertNull(writer.consumeEntry());
	}

	/**
	 * Test threading.
	 * 
	 * @throws Exception
	 *             Necessary to handle thread exceptions
	 */
	@Test
	public final void testThreading() throws Exception {
		final List<Exception> exceptions = Collections.synchronizedList(new ArrayList<Exception>());

		ThreadGroup threadGroup = new ThreadGroup("logging threads");

		for (int i = 0; i < 10; ++i) {
			Thread thread = new Thread(threadGroup, new Runnable() {

				@Override
				public void run() {
					try {
						for (int n = 0; n < 100; ++n) {
							Logger.setWriter(new LoggingWriter());
							Logger.setLoggingLevel(ELoggingLevel.TRACE);
							Logger.info("Test threading! This is log entry {0}.", n);
						}
					} catch (Exception ex) {
						exceptions.add(ex);
					}
				}

			});
			thread.start();
		}

		while (threadGroup.activeCount() > 0) {
			Thread.sleep(10);
		}

		for (Exception exception : exceptions) {
			throw exception;
		}
	}

	private static class LoggingWriter implements ILoggingWriter {

		private String entry;

		@Override
		public final void write(final ELoggingLevel level, final String logEntry) {
			entry = logEntry;
		}

		public String consumeEntry() {
			String copy = entry;
			entry = null;
			return copy;
		}

	}

}
