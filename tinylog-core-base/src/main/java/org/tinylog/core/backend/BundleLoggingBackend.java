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

import java.util.Collections;
import java.util.List;

import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Wrapper for multiple {@link LoggingBackend LoggingBackends}.
 */
public class BundleLoggingBackend implements LoggingBackend {

	private final List<LoggingBackend> backends;

	/**
	 * @param backends Logging backends to combine
	 */
	public BundleLoggingBackend(List<LoggingBackend> backends) {
		this.backends = backends;
	}

	/**
	 * Gets all wrapped child logging backends.
	 *
	 * @return The wrapped child logging backends
	 */
	public List<LoggingBackend> getProviders() {
		return Collections.unmodifiableList(backends);
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		StackTraceLocation childLocation = location.push();
		for (LoggingBackend backend : backends) {
			backend.log(childLocation, tag, level, throwable, message, arguments, formatter);
		}
	}

}
