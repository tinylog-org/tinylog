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

package org.tinylog.core.providers;

import java.util.Collections;
import java.util.List;

import org.tinylog.core.Level;
import org.tinylog.core.format.message.MessageFormatter;
import org.tinylog.core.runtime.StackTraceLocation;

/**
 * Wrapper for multiple {@link LoggingProvider LoggingProviders}.
 */
public final class BundleLoggingProvider implements LoggingProvider {

	private final List<LoggingProvider> providers;

	/**
	 * @param providers Logging providers to combine
	 */
	public BundleLoggingProvider(List<LoggingProvider> providers) {
		this.providers = providers;
	}

	/**
	 * Gets all wrapped child logging providers.
	 *
	 * @return The wrapped child logging providers
	 */
	public List<LoggingProvider> getProviders() {
		return Collections.unmodifiableList(providers);
	}

	@Override
	public void log(StackTraceLocation location, String tag, Level level, Throwable throwable, Object message,
			Object[] arguments, MessageFormatter formatter) {
		StackTraceLocation childLocation = location.push();
		for (LoggingProvider provider : providers) {
			provider.log(childLocation, tag, level, throwable, message, arguments, formatter);
		}
	}

}
