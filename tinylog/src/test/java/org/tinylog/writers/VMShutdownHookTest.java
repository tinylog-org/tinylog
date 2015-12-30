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

package org.tinylog.writers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tinylog.AbstractTest;
import org.tinylog.Configuration;
import org.tinylog.LogEntry;

import mockit.Mock;
import mockit.MockUp;

/**
 * Tests for the VM shutdown hook.
 *
 * @see VMShutdownHook
 */
public class VMShutdownHookTest extends AbstractTest {

	private RuntimeMock runtimeMock;

	/**
	 * Set up the mock for {@link Runtime}.
	 */
	@Before
	public final void init() {
		runtimeMock = new RuntimeMock();
	}

	/**
	 * Tear down mock.
	 */
	@After
	public final void dispose() {
		runtimeMock.tearDown();
	}

	/**
	 * Test if the class is a valid utility class.
	 *
	 * @see AbstractTest#testIfValidUtilityClass(Class)
	 */
	@Test
	public final void testIfValidUtilityClass() {
		testIfValidUtilityClass(VMShutdownHook.class);
	}

	/**
	 * Test VM shutdown hook functionality.
	 */
	@Test
	public final void testShutdown() {
		ShutdownWriter writer1 = new ShutdownWriter();
		ShutdownWriter writer2 = new ShutdownWriter();

		assertEquals(0, runtimeMock.threads.size());

		/* Register one writer */

		VMShutdownHook.register(writer1);
		assertEquals(1, runtimeMock.threads.size());
		VMShutdownHook.unregister(writer1);
		assertEquals(0, runtimeMock.threads.size());

		/* Register two writers */

		VMShutdownHook.register(writer1);
		assertEquals(1, runtimeMock.threads.size());
		VMShutdownHook.register(writer2);
		assertEquals(1, runtimeMock.threads.size());
		VMShutdownHook.unregister(writer2);
		assertEquals(1, runtimeMock.threads.size());
		VMShutdownHook.unregister(writer1);
		assertEquals(0, runtimeMock.threads.size());

		/* Do shutdown */

		VMShutdownHook.register(writer1);
		assertEquals(1, runtimeMock.threads.size());
		runtimeMock.threads.get(0).run();
		assertEquals(1, writer1.closeCalls);
		assertEquals(0, writer2.closeCalls);
	}

	/**
	 * Test failed shutdown.
	 */
	@Test
	public final void testFailedShutdown() {
		ShutdownWriter writer1 = new ShutdownWriter() {

			@Override
			public void close() throws Exception {
				super.close();
				throw new IOException("Unknown error");
			}

		};

		ShutdownWriter writer2 = new ShutdownWriter();

		VMShutdownHook.register(writer1);
		VMShutdownHook.register(writer2);

		runtimeMock.threads.get(0).run();
		assertEquals("LOGGER ERROR: Failed to shutdown writer (" + IOException.class.getName() + ": Unknown error)", getErrorStream().nextLine());
		assertEquals(1, writer1.closeCalls);
		assertEquals(1, writer2.closeCalls);
	}

	/**
	 * Test registration and unregistration during shutdown.
	 */
	@Test
	public final void testRegistrationDuringShutdown() {
		ShutdownWriter writer1 = new ShutdownWriter() {

			@Override
			public void close() throws Exception {
				VMShutdownHook.register(this);
				super.close();
			}

		};
		ShutdownWriter writer2 = new ShutdownWriter() {

			@Override
			public void close() throws Exception {
				VMShutdownHook.unregister(this);
				super.close();
			}

		};

		VMShutdownHook.register(writer1);
		VMShutdownHook.register(writer2);

		runtimeMock.threads.get(0).run(); // Should not throw any exception but ignore any (un)registration attempts
		assertEquals(1, writer1.closeCalls);
		assertEquals(1, writer2.closeCalls);
	}

	private static class ShutdownWriter implements Writer {

		private int closeCalls;

		public ShutdownWriter() {
			closeCalls = 0;
		}

		@Override
		public Set<LogEntryValue> getRequiredLogEntryValues() {
			return Collections.emptySet();
		}

		@Override
		public void init(final Configuration configuration) {
			// Do nothing
		}

		@Override
		public void write(final LogEntry logEntry) {
			// Just ignore
		}

		@Override
		public void flush() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void close() throws Exception {
			++closeCalls;
		}

	}

	private static final class RuntimeMock extends MockUp<Runtime> {

		private final List<Thread> threads;

		public RuntimeMock() {
			threads = new ArrayList<>();
		}

		@Mock
		public void addShutdownHook(final Thread hook) {
			threads.add(hook);
		}

		@Mock
		public boolean removeShutdownHook(final Thread hook) {
			return threads.remove(hook);
		}

	}

}
