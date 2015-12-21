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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;

import org.apache.log4j.util.MappedResourceBundle;
import org.junit.Before;
import org.junit.Test;
import org.tinylog.AbstractTest;
import org.tinylog.Configurator;
import org.tinylog.Level;
import org.tinylog.LogEntry;
import org.tinylog.util.StoreWriter;

import mockit.NonStrictExpectations;

/**
 * Tests for Apache Log4j 1.x compatible parameterized logging with {@link MessageFormat} pattern syntax.
 *
 * @see LogMF
 */
public class LogMFTest extends AbstractTest {

	private StoreWriter writer;

	/**
	 * Set up writer.
	 */
	@Before
	public final void init() {
		writer = new StoreWriter();
		Configurator.defaultConfig().writer(writer).activate();
	}

	/**
	 * Test trace logging methods.
	 */
	@Test
	public final void testTrace() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		LogMF.trace(null, "{0}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogMF.trace(null, "{0}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.trace(null, "{0}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogMF.trace(null, "{0}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.trace(null, "{0}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.trace(null, "{0}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.trace(null, "{0}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0F), logEntry.getMessage());

		LogMF.trace(null, "{0}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0), logEntry.getMessage());

		LogMF.trace(null, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogMF.trace(null, "{0} and {1}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogMF.trace(null, "{0}, {1} and {2}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogMF.trace(null, "{0}, {1}, {2} and {3}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogMF.trace(null, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.trace(null, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test debug logging methods.
	 */
	@Test
	public final void testDebug() {
		Configurator.currentConfig().level(Level.DEBUG).activate();

		LogMF.debug(null, "{0}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogMF.debug(null, "{0}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.debug(null, "{0}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogMF.debug(null, "{0}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.debug(null, "{0}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.debug(null, "{0}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.debug(null, "{0}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0F), logEntry.getMessage());

		LogMF.debug(null, "{0}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0), logEntry.getMessage());

		LogMF.debug(null, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogMF.debug(null, "{0} and {1}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogMF.debug(null, "{0}, {1} and {2}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogMF.debug(null, "{0}, {1}, {2} and {3}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogMF.debug(null, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.debug(null, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test info logging methods.
	 */
	@Test
	public final void testInfo() {
		Configurator.currentConfig().level(Level.INFO).activate();

		LogMF.info(null, "{0}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogMF.info(null, "{0}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.info(null, "{0}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogMF.info(null, "{0}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.info(null, "{0}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.info(null, "{0}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.info(null, "{0}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0F), logEntry.getMessage());

		LogMF.info(null, "{0}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0), logEntry.getMessage());

		LogMF.info(null, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogMF.info(null, "{0} and {1}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogMF.info(null, "{0}, {1} and {2}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogMF.info(null, "{0}, {1}, {2} and {3}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogMF.info(null, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.info(null, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test warning logging methods.
	 */
	@Test
	public final void testWarning() {
		Configurator.currentConfig().level(Level.WARNING).activate();

		LogMF.warn(null, "{0}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogMF.warn(null, "{0}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.warn(null, "{0}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogMF.warn(null, "{0}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.warn(null, "{0}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.warn(null, "{0}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.warn(null, "{0}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0F), logEntry.getMessage());

		LogMF.warn(null, "{0}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0), logEntry.getMessage());

		LogMF.warn(null, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogMF.warn(null, "{0} and {1}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogMF.warn(null, "{0}, {1} and {2}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogMF.warn(null, "{0}, {1}, {2} and {3}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogMF.warn(null, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.warn(null, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test error logging methods.
	 */
	@Test
	public final void testError() {
		Configurator.currentConfig().level(Level.ERROR).activate();

		LogMF.error(null, "{0} {1}!", new Object[] { "Hello", "World" });
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.error(null, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test fatal logging methods.
	 */
	@Test
	public final void testFatal() {
		Configurator.currentConfig().level(Level.ERROR).activate();

		LogMF.fatal(null, "{0} {1}!", new Object[] { "Hello", "World" });
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.fatal(null, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test generic logging methods.
	 */
	@Test
	public final void testGeneric() {
		Configurator.currentConfig().level(Level.INFO).activate();

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0F), logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0), logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "Hello {0}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0} and {1}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}, {1} and {2}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0}, {1}, {2} and {3}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogMF.log(null, org.apache.log4j.Level.INFO, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.log(null, org.apache.log4j.Level.INFO, throwable, "{0} {1}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test localized logging methods with activated severity level.
	 */
	@Test
	public final void testLocalized() {
		final MappedResourceBundle bundle = new MappedResourceBundle();
		bundle.put("single", "{0}");
		bundle.put("double", "{0} and {1}");
		bundle.put("triple", "{0}, {1} and {2}");
		bundle.put("quadruple", "{0}, {1}, {2} and {3}");

		new NonStrictExpectations(ResourceBundle.class) {
			{
				ResourceBundle.getBundle(anyString);
				result = bundle;
			}
		};

		Configurator.currentConfig().level(Level.INFO).activate();

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0F), logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(NumberFormat.getInstance().format(42.0), logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", "Hello");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "double", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "triple", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "quadruple", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogMF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", new Object[] { "Hello" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogMF.logrb(null, org.apache.log4j.Level.INFO, throwable, "test", "single", new Object[] { "Hello" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello", logEntry.getMessage());
		assertSame(throwable, logEntry.getException());
	}

	/**
	 * Test getting a message pattern from a {@code null} resource bundle.
	 */
	@Test
	public final void testNullBundle() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		LogMF.logrb(null, org.apache.log4j.Level.DEBUG, null, "single", 42);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("single", logEntry.getMessage());
	}

	/**
	 * Test getting a message pattern from a non-existent resource bundle.
	 */
	@Test
	public final void testMissingBundle() {
		Configurator.currentConfig().level(Level.TRACE).activate();

		LogMF.logrb(null, org.apache.log4j.Level.DEBUG, "test", "single", 42);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("single", logEntry.getMessage());
	}

	/**
	 * Test getting a message pattern from a non-existent mapping.
	 */
	@Test
	public final void testMissingKey() {
		final MappedResourceBundle bundle = new MappedResourceBundle();

		new NonStrictExpectations(ResourceBundle.class) {
			{
				ResourceBundle.getBundle(anyString);
				result = bundle;
			}
		};

		Configurator.currentConfig().level(Level.TRACE).activate();

		LogMF.logrb(null, org.apache.log4j.Level.DEBUG, "test", "single", 42);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("single", logEntry.getMessage());

		bundle.put("single", "{0}");

		LogMF.logrb(null, org.apache.log4j.Level.DEBUG, "test", "single", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());
	}

}
