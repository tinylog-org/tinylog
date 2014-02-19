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

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.pmw.tinylog.util.StringListOutputStream;
import org.pmw.tinylog.writers.LoggingWriter;
import org.pmw.tinylog.writers.VMShutdownHook;

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
		Configurator.defaultConfig().activate();
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
		System.setProperties(originProperties);
		System.setOut(originOutStream);
		System.setErr(originErrStream);
		assertFalse(systemOutputStream.toString(), systemOutputStream.hasLines());
		assertFalse(systemErrorStream.toString(), systemErrorStream.hasLines());

		try {
			Collection<LoggingWriter> openWriters = getOpenWriters();
			for (LoggingWriter writer : openWriters) {
				try {
					writer.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			assertThat("All writers must be closed", openWriters, empty());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	/**
	 * {@link System#out} is piped into this stream.
	 * 
	 * @return Result stream of {@link System#out}
	 */
	public final StringListOutputStream getOutputStream() {
		return systemOutputStream;
	}

	/**
	 * {@link System#err} is piped into this stream.
	 * 
	 * @return Result stream of {@link System#err}
	 */
	public final StringListOutputStream getErrorStream() {
		return systemErrorStream;
	}

	/**
	 * Test if a class is a valid utility class. A valid utility class must be final and has exactly one private
	 * constructor.
	 * 
	 * @param clazz
	 *            Class to test
	 */
	protected static final void testIfValidUtilityClass(final Class<?> clazz) {
		assertTrue("A utility class must be final", Modifier.isFinal(clazz.getModifiers()));

		Constructor<?>[] constructors = clazz.getDeclaredConstructors();
		assertTrue("A utility class must have exactly one private constructor", constructors.length == 1);
		Constructor<?> constructor = constructors[0];
		assertTrue("A utility class must have exactly one private constructor", Modifier.isPrivate(constructor.getModifiers()));

		try {
			constructor.setAccessible(true);
			constructor.newInstance();
		} catch (Exception ex) {
			fail("Failed to call constructor: " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<LoggingWriter> getOpenWriters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = VMShutdownHook.class.getDeclaredField("writers");
		field.setAccessible(true);
		return new ArrayList<LoggingWriter>((Collection<LoggingWriter>) field.get(null));
	}

}
