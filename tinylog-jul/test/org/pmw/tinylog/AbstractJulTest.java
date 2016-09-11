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

package org.pmw.tinylog;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.pmw.tinylog.util.logging.LogEntry;
import org.pmw.tinylog.util.logging.StorageHandler;

/**
 * Base class for all java.util.logging tests.
 */
public abstract class AbstractJulTest extends AbstractCoreTest {

	private static final Logger rootLogger = Logger.getLogger("");

	private StorageHandler storageHandler = new StorageHandler();

	/**
	 * Store all existing handlers and replace them with a {@link StorageHandler}.
	 */
	@Before
	public final void replaceHandlers() {
		LogManager.getLogManager().reset();
		rootLogger.addHandler(storageHandler);
	}

	/**
	 * Verify that all log entries have been consumed.
	 */
	@After
	public final void verifyLogEntries() {
		Collection<LogEntry> entries = consumeLogEntries();
		assertTrue(entries.toString(), entries.isEmpty());
	}
	
	/**
	 * Get and remove all stored log entries.
	 * 
	 * @return Stored log entries
	 */
	protected final Collection<LogEntry> consumeLogEntries() {
		return storageHandler.consumeLogEntries();
	}

}
