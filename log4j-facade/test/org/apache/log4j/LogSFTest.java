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

import java.util.ResourceBundle;

import mockit.NonStrictExpectations;

import org.junit.Before;
import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.LogEntry;
import org.pmw.tinylog.util.MappedResourceBundle;
import org.pmw.tinylog.util.StoreWriter;

/**
 * Tests for Apache Log4j 1.x compatible parameterized logging with SLF4J pattern syntax.
 *
 * @see LogSF
 */
public class LogSFTest extends AbstractTest {

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

		LogSF.trace(null, "{}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogSF.trace(null, "{}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.trace(null, "{}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogSF.trace(null, "{}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.trace(null, "{}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.trace(null, "{}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.trace(null, "{}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals(Float.toString(42.0F), logEntry.getMessage());

		LogSF.trace(null, "{}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals(Double.toString(42.0), logEntry.getMessage());

		LogSF.trace(null, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogSF.trace(null, "{} and {}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogSF.trace(null, "{}, {} and {}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogSF.trace(null, "{}, {}, {} and {}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogSF.trace(null, "{} {}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.TRACE, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.trace(null, throwable, "{} {}!", new Object[] { "Hello", "World" });
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

		LogSF.debug(null, "{}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogSF.debug(null, "{}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.debug(null, "{}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogSF.debug(null, "{}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.debug(null, "{}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.debug(null, "{}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.debug(null, "{}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals(Float.toString(42.0F), logEntry.getMessage());

		LogSF.debug(null, "{}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals(Double.toString(42.0), logEntry.getMessage());

		LogSF.debug(null, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogSF.debug(null, "{} and {}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogSF.debug(null, "{}, {} and {}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogSF.debug(null, "{}, {}, {} and {}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogSF.debug(null, "{} {}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.debug(null, throwable, "{} {}!", new Object[] { "Hello", "World" });
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

		LogSF.info(null, "{}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogSF.info(null, "{}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.info(null, "{}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogSF.info(null, "{}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.info(null, "{}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.info(null, "{}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.info(null, "{}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Float.toString(42.0F), logEntry.getMessage());

		LogSF.info(null, "{}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Double.toString(42.0), logEntry.getMessage());

		LogSF.info(null, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogSF.info(null, "{} and {}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogSF.info(null, "{}, {} and {}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogSF.info(null, "{}, {}, {} and {}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogSF.info(null, "{} {}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.info(null, throwable, "{} {}!", new Object[] { "Hello", "World" });
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

		LogSF.warn(null, "{}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogSF.warn(null, "{}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.warn(null, "{}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogSF.warn(null, "{}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.warn(null, "{}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.warn(null, "{}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.warn(null, "{}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(Float.toString(42.0F), logEntry.getMessage());

		LogSF.warn(null, "{}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals(Double.toString(42.0), logEntry.getMessage());

		LogSF.warn(null, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogSF.warn(null, "{} and {}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogSF.warn(null, "{}, {} and {}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogSF.warn(null, "{}, {}, {} and {}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogSF.warn(null, "{} {}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.WARNING, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.warn(null, throwable, "{} {}!", new Object[] { "Hello", "World" });
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

		LogSF.error(null, "{} {}!", new Object[] { "Hello", "World" });
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.error(null, throwable, "{} {}!", new Object[] { "Hello", "World" });
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

		LogSF.fatal(null, "{} {}!", new Object[] { "Hello", "World" });
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.ERROR, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.fatal(null, throwable, "{} {}!", new Object[] { "Hello", "World" });
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

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Float.toString(42.0F), logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Double.toString(42.0), logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "Hello {}!", "World");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{} and {}", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}, {} and {}", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{}, {}, {} and {}", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogSF.log(null, org.apache.log4j.Level.INFO, "{} {}!", new Object[] { "Hello", "World" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello World!", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.log(null, org.apache.log4j.Level.INFO, throwable, "{} {}!", new Object[] { "Hello", "World" });
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
		bundle.put("single", "{}");
		bundle.put("double", "{} and {}");
		bundle.put("triple", "{}, {} and {}");
		bundle.put("quadruple", "{}, {}, {} and {}");

		new NonStrictExpectations(ResourceBundle.class) {
			{
				ResourceBundle.getBundle(anyString);
				result = bundle;
			}
		};

		Configurator.currentConfig().level(Level.INFO).activate();

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", true);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("true", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", (byte) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 'X');
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("X", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", (short) 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42L);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42.0F);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Float.toString(42.0F), logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", 42.0);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals(Double.toString(42.0), logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", "Hello");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "double", "A", "B");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A and B", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "triple", "A", "B", "C");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B and C", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "quadruple", "A", "B", "C", "D");
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("A, B, C and D", logEntry.getMessage());

		LogSF.logrb(null, org.apache.log4j.Level.INFO, "test", "single", new Object[] { "Hello" });
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.INFO, logEntry.getLevel());
		assertEquals("Hello", logEntry.getMessage());

		Throwable throwable = new Throwable();
		LogSF.logrb(null, org.apache.log4j.Level.INFO, throwable, "test", "single", new Object[] { "Hello" });
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

		LogSF.logrb(null, org.apache.log4j.Level.DEBUG, null, "single", 42);
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

		LogSF.logrb(null, org.apache.log4j.Level.DEBUG, "test", "single", 42);
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

		LogSF.logrb(null, org.apache.log4j.Level.DEBUG, "test", "single", 42);
		LogEntry logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("single", logEntry.getMessage());

		bundle.put("single", "{}");

		LogSF.logrb(null, org.apache.log4j.Level.DEBUG, "test", "single", 42);
		logEntry = writer.consumeLogEntry();
		assertEquals(Level.DEBUG, logEntry.getLevel());
		assertEquals("42", logEntry.getMessage());
	}

}
