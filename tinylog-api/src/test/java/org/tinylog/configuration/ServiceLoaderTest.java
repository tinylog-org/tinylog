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

package org.tinylog.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

/**
 * Tests for {@link ServiceLoader}.
 */
@RunWith(PowerMockRunner.class)
public final class ServiceLoaderTest {

	private static final String SERVICE_PREFIX = "META-INF/services/";

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Creates pre-filled service file for {@link List} in class path.
	 *
	 * @throws Exception
	 *             Failed creating service file
	 */
	@Before
	public void init() throws Exception {
		FileSystem.createServiceFile(List.class, ArrayList.class.getName(), LinkedList.class.getName());
	}

	/**
	 * Deletes created service file from class path.
	 *
	 * @throws Exception
	 *             Failed deleting service file
	 */
	@After
	public void clear() throws Exception {
		FileSystem.deleteServiceFile(List.class);
	}

	/**
	 * Verifies that service loader works if there are no registered services.
	 *
	 * @throws Exception
	 *             Failed deleting service file
	 */
	@Test
	public void noRegisteredServices() throws Exception {
		FileSystem.deleteServiceFile(List.class);

		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.createAll()).isEmpty();
	}

	/**
	 * Verifies that service implementations can be found by acronym.
	 */
	@Test
	public void getServiceByAcronym() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.create("array")).isInstanceOf(ArrayList.class);
		assertThat(loader.create("linked")).isInstanceOf(LinkedList.class);
	}

	/**
	 * Verifies that service implementations can be found by class name.
	 */
	@Test
	public void getServiceByClassName() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.create(ArrayList.class.getName())).isInstanceOf(ArrayList.class);
		assertThat(loader.create(LinkedList.class.getName())).isInstanceOf(LinkedList.class);
	}

	/**
	 * Verifies that service loader returns an instance for all registered services.
	 */
	@Test
	public void getAllServices() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.createAll())
			.hasSize(2)
			.hasAtLeastOneElementOfType(ArrayList.class)
			.hasAtLeastOneElementOfType(LinkedList.class);
	}

	/**
	 * Verifies that service files with empty lines can be parsed without any errors.
	 *
	 * @throws Exception
	 *             Failed overriding service file
	 */
	@Test
	public void emptyLines() throws Exception {
		FileSystem.createServiceFile(List.class, "", ArrayList.class.getName(), "");

		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.createAll())
			.hasSize(1)
			.hasAtLeastOneElementOfType(ArrayList.class);
	}

	/**
	 * Verifies that service files with comments can be parsed without any errors.
	 *
	 * @throws Exception
	 *             Failed overriding service file
	 */
	@Test
	public void comments() throws Exception {
		FileSystem.createServiceFile(List.class, "#Comment", ArrayList.class.getName());

		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.createAll())
			.hasSize(1)
			.hasAtLeastOneElementOfType(ArrayList.class);
	}

	/**
	 * Verifies that an accurate error message will be output, if there is no registered service for an acronym.
	 */
	@Test
	public void invalidAcronym() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.create("test")).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce("test");
	}

	/**
	 * Verifies that an accurate error message will be output, if a service implementation class does not exist.
	 */
	@Test
	public void nonExistentClassName() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.create("my.package.Test")).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce("my.package.Test");
	}

	/**
	 * Verifies that an accurate error message will be output, if a service implementation class does not implement the
	 * required service interface.
	 */
	@Test
	public void notImplementingInterface() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.create(HashSet.class.getName())).isNull();

		assertThat(systemStream.consumeErrorOutput())
			.contains("ERROR")
			.containsOnlyOnce(HashSet.class.getName())
			.containsOnlyOnce(List.class.getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if a service implementation class is abstract.
	 */
	@Test
	public void abstractServiceClass() {
		String className = AbstractListWithPublicConstructor.class.getName();
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);

		assertThat(loader.create(className)).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(className);
	}

	/**
	 * Verifies that an accurate error message will be output, if a service implementation has no matching constructor
	 * for the passed arguments.
	 */
	@Test
	public void withoutMatchingConstructor() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class, int.class);
		assertThat(loader.create(LinkedList.class.getName(), 1)).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(LinkedList.class.getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if a constructor of a service implementation is not
	 * public.
	 */
	@Test
	public void invisibleConstructor() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.create(Collections.EMPTY_LIST.getClass().getName())).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(Collections.EMPTY_LIST.getClass().getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if the number of passed arguments is not equal to the
	 * number of specified arguments.
	 */
	@Test
	public void wrongNumberOfArguments() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class, int.class);
		assertThat(loader.create(ArrayList.class.getName())).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(ArrayList.class.getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if an argument has not the expected type.
	 */
	@Test
	public void invalidArgumentType() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class, int.class);
		assertThat(loader.create(ArrayList.class.getName(), "test")).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(ArrayList.class.getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if an argument has an invalid value.
	 */
	@Test
	public void invalidArgumentValue() {
		ServiceLoader<?> loader = new ServiceLoader<>(List.class, int.class);
		assertThat(loader.create(ArrayList.class.getName(), -1)).isNull();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(ArrayList.class.getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if service files can be not loaded as resources.
	 *
	 * @throws IOException
	 *             Failed invoking {@link ClassLoader#getSystemResources(String)}
	 */
	@Test
	@PrepareForTest(ServiceLoader.class)
	public void loadingServiceFilesFails() throws IOException {
		mockStatic(ClassLoader.class);
		when(ClassLoader.getSystemResources(anyString())).thenThrow(new IOException());

		ServiceLoader<?> loader = new ServiceLoader<>(List.class);
		assertThat(loader.createAll()).isEmpty();
		assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(SERVICE_PREFIX + List.class.getName());
	}

	/**
	 * Verifies that an accurate error message will be output, if a service file can be not open for reading.
	 *
	 * @throws Exception
	 *             Failed mocking reader
	 */
	@Test
	@PrepareForTest(ServiceLoader.class)
	public void readingServiceFileFails() throws Exception {
		whenNew(BufferedReader.class).withAnyArguments().thenThrow(new IOException());
		try {
			ServiceLoader<?> loader = new ServiceLoader<>(List.class);
			assertThat(loader.createAll()).isEmpty();
			assertThat(systemStream.consumeErrorOutput()).contains("ERROR").containsOnlyOnce(SERVICE_PREFIX + List.class.getName());
		} finally {
			ArgumentCaptor<Reader> captor = ArgumentCaptor.forClass(Reader.class);
			verifyNew(BufferedReader.class).withArguments(captor.capture());
			captor.getValue().close();
		}
	}

	/**
	 * Dummy class for testing to load an abstract service class with visible constructor.
	 */
	public abstract static class AbstractListWithPublicConstructor extends AbstractList<Object> {

		/** */
		public AbstractListWithPublicConstructor() {
		}

	}

}
