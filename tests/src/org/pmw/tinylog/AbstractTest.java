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

import java.io.PrintStream;
import java.util.Properties;

import mockit.Mockit;

import org.junit.After;
import org.junit.Before;
import org.pmw.tinylog.util.StringListOutputStream;

import static org.junit.Assert.assertFalse;

/**
 * Base class for all tests.
 */
public abstract class AbstractTest {

	private StringListOutputStream systemOutputStream;
	private StringListOutputStream systemErrorStream;

	private Properties originProperties;
	private PrintStream originOutStream;
	private PrintStream originErrStream;

	/**
	 * Reconfigure {@link System}.
	 */
	@Before
	public final void setUp() {
		originProperties = (Properties) System.getProperties().clone();
		originOutStream = System.out;
		originErrStream = System.err;
		systemOutputStream = new StringListOutputStream();
		systemErrorStream = new StringListOutputStream();
		System.setOut(new PrintStream(systemOutputStream, true));
		System.setErr(new PrintStream(systemErrorStream, true));
	}

	/**
	 * Reset {@link System}.
	 */
	@After
	public final void tearDown() {
		Mockit.tearDownMocks();
		System.setProperties(originProperties);
		System.setOut(originOutStream);
		System.setErr(originErrStream);
		assertFalse(systemOutputStream.toString(), systemOutputStream.hasLines());
		assertFalse(systemErrorStream.toString(), systemErrorStream.hasLines());
	}

	/**
	 * {@link System#out} is piped into this stream.
	 * 
	 * @return Result stream of {@link System#out}
	 */
	public final StringListOutputStream getSystemOutputStream() {
		return systemOutputStream;
	}

	/**
	 * {@link System#err} is piped into this stream.
	 * 
	 * @return Result stream of {@link System#err}
	 */
	public final StringListOutputStream getSystemErrorStream() {
		return systemErrorStream;
	}

}
