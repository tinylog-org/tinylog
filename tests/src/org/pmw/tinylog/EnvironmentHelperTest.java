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

package org.pmw.tinylog;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.pmw.tinylog.hamcrest.RegexMatcher.matches;

import java.lang.management.ManagementFactory;

import mockit.NonStrictExpectations;

import org.junit.Test;

/**
 * Tests the environment helper.
 * 
 * @see EnvironmentHelper
 */
public class EnvironmentHelperTest extends AbstractTest {

	/**
	 * Test if the class is a valid utility class.
	 * 
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(EnvironmentHelper.class);
	}

	/**
	 * Test getting the current process ID.
	 */
	@Test
	public final void testCurrentProcessId() {
		Object pid = EnvironmentHelper.getProcessId();
		assertThat(pid, instanceOf(String.class));
		assertThat((String) pid, matches("\\d+"));
		assertThat(ManagementFactory.getRuntimeMXBean().getName(), startsWith((String) pid));
	}

	/**
	 * Test use case if {@link ManagementFactory.getRuntimeMXBean().getName()} returns only the process ID.
	 */
	@Test
	public final void testProcessIdWithoutHost() {
		new NonStrictExpectations(ManagementFactory.getRuntimeMXBean()) {

			{
				ManagementFactory.getRuntimeMXBean().getName();
				returns("1234");
			}

		};

		assertEquals("1234", EnvironmentHelper.getProcessId());
	}

	/**
	 * Test use case if {@link ManagementFactory.getRuntimeMXBean().getName()} returns process ID plus host name.
	 */
	@Test
	public final void testProcessIdWithHost() {
		new NonStrictExpectations(ManagementFactory.getRuntimeMXBean()) {

			{
				ManagementFactory.getRuntimeMXBean().getName();
				returns("5678@localhost");
			}

		};

		assertEquals("5678", EnvironmentHelper.getProcessId());
	}

	/**
	 * Test getting the process ID for Android.
	 */
	@Test
	public final void testProcessForAndroid() {
		String runtime = System.getProperty("java.runtime.name");
		try {
			System.setProperty("java.runtime.name", "Android Runtime");
			Object pid = EnvironmentHelper.getProcessId();
			assertThat(pid, instanceOf(Integer.class));
			assertThat(ManagementFactory.getRuntimeMXBean().getName(), startsWith(pid.toString()));
		} finally {
			System.setProperty("java.runtime.name", runtime);
		}
	}

}
