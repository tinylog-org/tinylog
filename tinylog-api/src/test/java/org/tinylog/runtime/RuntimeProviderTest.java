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

package org.tinylog.runtime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RuntimeProvider}.
 */
public final class RuntimeProviderTest {

	private String runtime;

	/**
	 * Stores backup of runtime name system property.
	 */
	@Before
	public void init() {
		runtime = System.getProperty("java.runtime.name");
	}

	/**
	 * Resets runtime name system property.
	 */
	@After
	public void reset() {
		System.setProperty("java.runtime.name", runtime);
	}

	/**
	 * Verifies that a runtime dialect for the current VM will be returned.
	 */
	@Test
	public void getDialect() {
		assertThat(RuntimeProvider.getDialect()).isNotNull();
	}

	/**
	 * Verifies that {@link AndroidRuntime} will be resolved as runtime dialect in Android Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectAndroidRuntime() throws Exception {
		System.setProperty("java.runtime.name", "Android Runtime");
		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(AndroidRuntime.class);
	}

	/**
	 * Verifies that {@link JavaRuntime} will be resolved as runtime dialect in Sun or Oracle Java Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectOracleJavaRuntime() throws Exception {
		System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(JavaRuntime.class);
	}

	/**
	 * Verifies that {@link JavaRuntime} will be resolved as runtime dialect in OpenJDK Java Virtual Machines.
	 *
	 * @throws Exception
	 *             Failed invoking private method {@link RuntimeProvider#resolveDialect()}
	 */
	@Test
	public void detectOpenJdkRuntime() throws Exception {
		System.setProperty("java.runtime.name", "OpenJDK Runtime Environment");
		RuntimeDialect dialect = Whitebox.invokeMethod(RuntimeProvider.class, "resolveDialect");
		assertThat(dialect).isInstanceOf(JavaRuntime.class);
	}

}
