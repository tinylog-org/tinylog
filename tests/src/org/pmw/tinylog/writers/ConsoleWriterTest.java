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

package org.pmw.tinylog.writers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.util.SilentOutputStream;

/**
 * Tests for the console logging writer.
 * 
 * @see org.pmw.tinylog.writers.ConsoleWriter
 */
public class ConsoleWriterTest {

	private ConsoleWriter writer;

	private PrintStream defaultErrorStream;
	private PrintStream defaultOutputStream;

	private SilentOutputStream errorStream;
	private SilentOutputStream outputStream;

	/**
	 * Bypass output streams.
	 */
	@Before
	public final void init() {
		writer = new ConsoleWriter();

		defaultErrorStream = System.err;
		errorStream = new SilentOutputStream();
		System.setErr(new PrintStream(errorStream));

		defaultOutputStream = System.out;
		outputStream = new SilentOutputStream();
		System.setOut(new PrintStream(outputStream));
	}

	/**
	 * Reset the system output streams.
	 */
	@After
	public final void dispose() {
		System.setOut(defaultOutputStream);
		System.setErr(defaultErrorStream);
	}

	/**
	 * Test if error messages will appear in the "error" output stream.
	 */
	@Test
	public final void testErrorStream() {
		writer.write(ELoggingLevel.ERROR, "Hello\n");

		assertTrue(errorStream.isUsed());
		assertFalse(outputStream.isUsed());
	}

	/**
	 * Test if info messages will appear in the "standard" output stream.
	 */
	@Test
	public final void testOutputStream() {
		writer.write(ELoggingLevel.INFO, "Hello\n");

		assertFalse(errorStream.isUsed());
		assertTrue(outputStream.isUsed());
	}

}
