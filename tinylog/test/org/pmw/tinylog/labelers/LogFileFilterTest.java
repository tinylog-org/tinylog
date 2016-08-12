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

package org.pmw.tinylog.labelers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;

/**
 * Tests for log file filter.
 * 
 * @see LogFileFilter
 */
public class LogFileFilterTest extends AbstractTest {

	/**
	 * Test matching of filter.
	 */
	@Test
	public final void testMatching() {
		LogFileFilter filter = new LogFileFilter("log", ".txt");

		assertTrue(filter.accept(new File("log.txt")));
		assertTrue(filter.accept(new File("log.1.txt")));

		assertFalse(filter.accept(new File("txt.log")));
		assertFalse(filter.accept(new File("log.txt.1")));
		assertFalse(filter.accept(new File(".txt")));
		assertFalse(filter.accept(new File("log")));
	}

}
