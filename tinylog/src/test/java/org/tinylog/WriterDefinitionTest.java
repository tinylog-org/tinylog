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

package org.tinylog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;
import org.tinylog.writers.ConsoleWriter;
import org.tinylog.writers.Writer;

/**
 * Tests for writer definition.
 *
 * @see WriterDefinition
 */
public class WriterDefinitionTest {
	
	/**
	 * Test writer definition without defined level and format pattern.
	 */
	@Test
	public final void testOnlyWriter() {
		Writer writer = new ConsoleWriter();
		WriterDefinition definition = new WriterDefinition(writer);
		assertSame(writer, definition.getWriter());
		assertNull(definition.getLevel());
		assertNull(definition.getFormatPattern());
		
		definition = definition.fill(Level.ERROR, "b");
		assertSame(writer, definition.getWriter());
		assertEquals(Level.ERROR, definition.getLevel());
		assertEquals("b", definition.getFormatPattern());
	}

	/**
	 * Test writer definition without defined format pattern.
	 */
	@Test
	public final void testWithoutFormatPattern() {
		Writer writer = new ConsoleWriter();
		WriterDefinition definition = new WriterDefinition(writer, Level.TRACE);
		assertSame(writer, definition.getWriter());
		assertEquals(Level.TRACE, definition.getLevel());
		assertNull(definition.getFormatPattern());
		
		definition = definition.fill(Level.ERROR, "b");
		assertSame(writer, definition.getWriter());
		assertEquals(Level.TRACE, definition.getLevel());
		assertEquals("b", definition.getFormatPattern());
	}

	/**
	 * Test writer definition without defined level.
	 */
	@Test
	public final void testWithoutLevel() {	
		Writer writer = new ConsoleWriter();
		WriterDefinition definition = new WriterDefinition(writer, "a");
		assertSame(writer, definition.getWriter());
		assertNull(definition.getLevel());
		assertEquals("a", definition.getFormatPattern());
		
		definition = definition.fill(Level.ERROR, "b");
		assertSame(writer, definition.getWriter());
		assertEquals(Level.ERROR, definition.getLevel());
		assertEquals("a", definition.getFormatPattern());
	}

	/**
	 * Test writer definition with defined level and format pattern.
	 */
	@Test
	public final void testFull() {		
		Writer writer = new ConsoleWriter();
		WriterDefinition definition = new WriterDefinition(writer, Level.TRACE, "a");
		assertSame(writer, definition.getWriter());
		assertEquals(Level.TRACE, definition.getLevel());
		assertEquals("a", definition.getFormatPattern());
		
		definition = definition.fill(Level.ERROR, "b");
		assertSame(writer, definition.getWriter());
		assertEquals(Level.TRACE, definition.getLevel());
		assertEquals("a", definition.getFormatPattern());
	}

}
