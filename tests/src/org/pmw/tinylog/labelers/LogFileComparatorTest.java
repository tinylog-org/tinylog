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

import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for log file comparator.
 * 
 * @see LogFileComparator
 */
public class LogFileComparatorTest extends AbstractTest {

	private File file1;
	private File file2;

	/**
	 * Create files.
	 * 
	 * @throws IOException
	 *             Failed to create files
	 */
	@Before
	public final void init() throws IOException {
		file1 = FileHelper.createTemporaryFile("tmp");
		file2 = FileHelper.createTemporaryFile("tmp");
	}

	/**
	 * Test compare with the youngest file first.
	 */
	@Test
	public final void testYoungestFirst() {
		assertTrue(file1.setLastModified(0L));
		assertTrue(file2.setLastModified(1L));

		LogFileComparator comparator = LogFileComparator.getInstance();
		assertNotNull(comparator);
		/* Greater than 0 means that the first file is the youngest */
		assertThat(comparator.compare(file1, file2), greaterThan(0));
	}

	/**
	 * The compare with two files that have the same age.
	 */
	@Test
	public final void testSameAge() {
		assertTrue(file1.setLastModified(1L));
		assertTrue(file2.setLastModified(1L));

		LogFileComparator comparator = LogFileComparator.getInstance();
		assertNotNull(comparator);
		assertEquals(0, comparator.compare(file1, file2));
	}

	/**
	 * Test compare with the oldest file first.
	 */
	@Test
	public final void testOldestFirst() {
		assertTrue(file1.setLastModified(1L));
		assertTrue(file2.setLastModified(0L));

		LogFileComparator comparator = LogFileComparator.getInstance();
		assertNotNull(comparator);
		/* Less than 0 means that the second file is the youngest */
		assertThat(comparator.compare(file1, file2), lessThan(0));
	}

}
