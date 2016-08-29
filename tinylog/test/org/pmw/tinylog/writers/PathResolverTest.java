/*
 * Copyright 2016 Martin Winandy
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

import static org.junit.Assert.assertEquals;

import java.util.Map.Entry;

import org.junit.Test;
import org.pmw.tinylog.AbstractCoreTest;

/**
 * Tests for path resolver.
 *
 * @see PathResolver
 */
public class PathResolverTest extends AbstractCoreTest {
	
	/**
	 * Test keeping a path without any placeholder.
	 */
	@Test
	public final void testWithoutVariables() {
		assertEquals("test.log", PathResolver.resolve("test.log"));
	}
	
	/**
	 * Test resolving a path with a single placeholder for a system property.
	 */
	@Test
	public final void testResolvingSingleVariable() {
		System.setProperty("LOG_FILE_NAME", "test.log");
		
		assertEquals("test.log", PathResolver.resolve("${LOG_FILE_NAME}"));
	}
	
	/**
	 * Test resolving a path with two placeholders for system properties.
	 */
	@Test
	public final void testResolvingMultipleVariables() {
		System.setProperty("LOG_FOLDER", "logs");
		System.setProperty("LOG_NAME", "test");
		
		assertEquals("../logs/test.log", PathResolver.resolve("../${LOG_FOLDER}/${LOG_NAME}.log"));
	}
	
	/**
	 * Test resolving a path with a single placeholder for a environment variable.
	 */
	@Test
	public final void testResolvingEnvironmentVariable() {
		Entry<String, String> entry = System.getenv().entrySet().iterator().next();
		
		assertEquals(entry.getValue(), PathResolver.resolve("${" + entry.getKey() + "}"));
	}
	
	/**
	 * Test if system property and environment variable are existing, system property should win.
	 */
	@Test
	public final void testSystemPropertyOverwritesEnvironmentVariable() {
		Entry<String, String> entry = System.getenv().entrySet().iterator().next();
		System.setProperty(entry.getKey(), entry.getValue() + "_2");
		
		assertEquals(entry.getValue() + "_2", PathResolver.resolve("${" + entry.getKey() + "}"));
	}
	
	/**
	 * Test handling a non-resolvable placeholder.
	 */
	@Test
	public final void testUndefinedVariable() {
		assertEquals("${LOG_FILE_NAME}", PathResolver.resolve("${LOG_FILE_NAME}"));
		assertEquals("LOGGER WARNING: \"LOG_FILE_NAME\" could not be found in system properties nor in environment variables", getErrorStream().nextLine());
	}
	
	/**
	 * Test handling an empty placeholder.
	 */
	@Test
	public final void testEmtyPlaceholder() {
		assertEquals("${}", PathResolver.resolve("${}"));
		assertEquals("LOGGER WARNING: Empty variable names cannot be resolved: ${}", getErrorStream().nextLine());
	}
	
	/**
	 * Test handling a placeholder without closing curly brace.
	 */
	@Test
	public final void testMissingClosingBrace() {
		assertEquals("${LOG_FILE_NAME", PathResolver.resolve("${LOG_FILE_NAME"));
		assertEquals("LOGGER WARNING: Closing curly brace is missing for: ${LOG_FILE_NAME", getErrorStream().nextLine());
	}

}
