/*
 * Copyright 2013 Martin Winandy
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

import org.junit.Test;
import org.tinylog.AbstractTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for the log manager.
 * 
 * @see LogManager
 */
public class LogManagerTest extends AbstractTest {

	/**
	 * Test the root logger.
	 */
	@Test
	public final void testRoot() {
		Logger root = LogManager.getRootLogger();
		assertNotNull(root);
		assertEquals("root", root.getName());
		assertNull(root.getParent());

		assertSame(root, LogManager.getRootLogger());
		assertSame(root, LogManager.getLogger("root"));
	}

	/**
	 * Test getting loggers for classes and names.
	 */
	@Test
	public final void testGettingLoggers() {
		Logger logManagerLogger = LogManager.getLogger(LogManagerTest.class);
		assertNotNull(logManagerLogger);
		assertEquals(LogManagerTest.class.getName(), logManagerLogger.getName());

		Logger orgLogger = LogManager.getLogger("org");
		assertNotNull(orgLogger);
		assertEquals("org", orgLogger.getName());

		assertNotSame(logManagerLogger, orgLogger);
	}

}
