/*
 * Copyright 2015 Martin Winandy
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

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.util.StoreWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for Apache Log4j 1.x compatible base class for parameterized logging.
 *
 * @see LogXF
 */
public class LogXFTest extends AbstractTest {
	
	private StoreWriter writer;

	/**
	 * Set up logger.
	 */
	@Before
	public final void init() {
		writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();
	}

	/**
	 * Test entering log methods.
	 */
	@Test
	public final void testEntering() {
		
		/* DEBUG */

		Configurator.currentConfig().level(org.pmw.tinylog.Level.DEBUG).activate();

		LogXF.entering(null, "Test", "do()");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", (String) null);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY null", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", "Hello");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY Hello", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", (Object) null);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY null", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY 42", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", EvilObject.INSTANCE);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY ?", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", (Object[]) null);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY null", logEntry.getMessage());

		LogXF.entering(null, "Test", "do()", new Object[] { 1, 2 });
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() ENTRY {1,2}", logEntry.getMessage());

		/* INFO */

		Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();

		LogXF.entering(null, "Test", "do()");
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", (String) null);
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", "Hello");
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", (Object) null);
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", 42);
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", EvilObject.INSTANCE);
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", (Object[]) null);
		assertNull(writer.consumeLogEntry());

		LogXF.entering(null, "Test", "do()", new Object[] { 1, 2 });
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test exiting log methods.
	 */
	@Test
	public final void testExiting() {

		/* DEBUG */

		Configurator.currentConfig().level(org.pmw.tinylog.Level.DEBUG).activate();

		LogXF.exiting(null, "Test", "do()");
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() RETURN", logEntry.getMessage());

		LogXF.exiting(null, "Test", "do()", (String) null);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() RETURN null", logEntry.getMessage());

		LogXF.exiting(null, "Test", "do()", "Hello");
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() RETURN Hello", logEntry.getMessage());

		LogXF.exiting(null, "Test", "do()", (Object) null);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() RETURN null", logEntry.getMessage());

		LogXF.exiting(null, "Test", "do()", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() RETURN 42", logEntry.getMessage());

		LogXF.exiting(null, "Test", "do()", EvilObject.INSTANCE);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() RETURN ?", logEntry.getMessage());

		/* INFO */

		Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();

		LogXF.exiting(null, "Test", "do()");
		assertNull(writer.consumeLogEntry());

		LogXF.exiting(null, "Test", "do()", (String) null);
		assertNull(writer.consumeLogEntry());

		LogXF.exiting(null, "Test", "do()", "Hello");
		assertNull(writer.consumeLogEntry());

		LogXF.exiting(null, "Test", "do()", (Object) null);
		assertNull(writer.consumeLogEntry());

		LogXF.exiting(null, "Test", "do()", 42);
		assertNull(writer.consumeLogEntry());

		LogXF.exiting(null, "Test", "do()", EvilObject.INSTANCE);
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * Test throwing log methods.
	 */
	@Test
	public final void testThrowing() {
		Throwable throwable = new Throwable();

		/* DEBUG */

		Configurator.currentConfig().level(org.pmw.tinylog.Level.DEBUG).activate();

		LogXF.throwing(null, "Test", "do()", null);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() THROW", logEntry.getMessage());
		assertNull(logEntry.getException());

		LogXF.throwing(null, "Test", "do()", throwable);
		logEntry = writer.consumeLogEntry();
		assertEquals(org.pmw.tinylog.Level.DEBUG, logEntry.getLevel());
		assertEquals("Test.do() THROW", logEntry.getMessage());
		assertEquals(throwable, logEntry.getException());

		/* INFO */

		Configurator.currentConfig().level(org.pmw.tinylog.Level.INFO).activate();

		LogXF.throwing(null, "Test", "do()", null);
		assertNull(writer.consumeLogEntry());

		LogXF.throwing(null, "Test", "do()", throwable);
		assertNull(writer.consumeLogEntry());
	}

	/**
	 * This class throws a {@link UnsupportedOperationException} if calling {@link #toString()}.
	 */
	private static final class EvilObject extends Object {

		private static final EvilObject INSTANCE = new EvilObject();

		/** */
		private EvilObject() {
		}

		@Override
		public String toString() {
			throw new UnsupportedOperationException();
		}

	}

}
