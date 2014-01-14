/*
 * Copyright 2014 Martin Winandy
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Test;
import org.pmw.tinylog.AbstractTest;
import org.pmw.tinylog.LoggingLevel;

import android.util.Log;

/**
 * Tests for the logcat logging writer for Android.
 * 
 * @see LogcatWriter
 */
public class LogcatWriterTest extends AbstractTest {

	/**
	 * Test setting and getting tag.
	 */
	@Test
	public final void testTag() {
		assertEquals("hello", new LogcatWriter("hello").getTag());
		assertEquals("test", new LogcatWriter("test").getTag());
	}

	/**
	 * Test logging.
	 */
	@Test
	public final void testLogging() {
		final LogcatWriter writer = new LogcatWriter("myapp");
		writer.init();

		assertEquals(0, Log.consumeEntries().size());

		writer.write(LoggingLevel.TRACE, "Hello World");
		assertThat(Log.consumeEntries(), is(Collections.singletonList("V\tmyapp\tHello World")));

		writer.write(LoggingLevel.DEBUG, "Hello World");
		assertThat(Log.consumeEntries(), is(Collections.singletonList("D\tmyapp\tHello World")));

		writer.write(LoggingLevel.INFO, "Hello World");
		assertThat(Log.consumeEntries(), is(Collections.singletonList("I\tmyapp\tHello World")));

		writer.write(LoggingLevel.WARNING, "Hello World");
		assertThat(Log.consumeEntries(), is(Collections.singletonList("W\tmyapp\tHello World")));

		writer.write(LoggingLevel.ERROR, "Hello World");
		assertThat(Log.consumeEntries(), is(Collections.singletonList("E\tmyapp\tHello World")));
	}

}
