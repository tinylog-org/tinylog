/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.backend;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BundleLoggingBackendTest {

	/**
	 * Verifies that all passed child logging backends are stored.
	 */
	@Test
	public void getChildProviders() {
		LoggingBackend first = mock(LoggingBackend.class);
		LoggingBackend second = mock(LoggingBackend.class);
		BundleLoggingBackend parent = new BundleLoggingBackend(Arrays.asList(first, second));
		assertThat(parent.getProviders()).containsExactlyInAnyOrder(first, second);
	}

	/**
	 * Verifies that log entries are passed to all assigned child backends.
	 */
	@Test
	public void provideLogsToChildren() {
		LoggingBackend first = mock(LoggingBackend.class);
		LoggingBackend second = mock(LoggingBackend.class);
		BundleLoggingBackend backend = new BundleLoggingBackend(Arrays.asList(first, second));

		StackTraceLocation location = mock(StackTraceLocation.class);
		Throwable throwable = new Throwable();
		Object[] arguments = {"world"};
		MessageFormatter formatter = mock(MessageFormatter.class);
		backend.log(location, "TEST", Level.INFO, throwable, "Hello {}!", arguments, formatter);

		verify(first).log(
			not(same(location)),
			eq("TEST"),
			eq(Level.INFO),
			same(throwable),
			eq("Hello {}!"),
			same(arguments),
			same(formatter)
		);

		verify(second).log(
			not(same(location)),
			eq("TEST"),
			eq(Level.INFO),
			same(throwable),
			eq("Hello {}!"),
			same(arguments),
			same(formatter)
		);

		verify(location).push();
	}

}
