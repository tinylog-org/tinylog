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

package org.tinylog.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.tinylog.Level;

/**
 * Registry for receiving the actual logging and context provider.
 *
 * <p>
 * All as service registered providers will be loaded from <tt>META-INF/services</tt>. Multiple providers of the same
 * type will be combined to one.
 * </p>
 */
public final class ProviderRegistry {

	private static final String WARNING_MESSAGE =
		"No logging framework implementation found in classpath. Add tinylog-backend.jar for outputting log entries.";

	private static final LoggingProvider LOGGING_PROVIDER = loadLoggingProvider();
	private static final ContextProvider CONTEXT_PROVIDER = loadContextProvider();

	/** */
	private ProviderRegistry() {
	}

	/**
	 * Returns the actual logging provider.
	 *
	 * <p>
	 * Multiple providers will be combined to one. If there are no logging providers, a stub implementation will be
	 * returned instead of {@code null}.
	 * </p>
	 *
	 * @return Actual logging provider
	 */
	public static LoggingProvider getLoggingProvider() {
		return LOGGING_PROVIDER;
	}

	/**
	 * Returns the actual context provider.
	 *
	 * <p>
	 * Multiple providers will be combined to one. If there are no context providers, a stub implementation will be
	 * returned instead of {@code null}.
	 * </p>
	 *
	 * @return Actual logging provider
	 */
	public static ContextProvider getContextProvider() {
		return CONTEXT_PROVIDER;
	}

	/**
	 * Loads the actual logging provider.
	 *
	 * @return New logging provider instance
	 */
	private static LoggingProvider loadLoggingProvider() {
		List<LoggingProvider> providers = new ArrayList<LoggingProvider>(1);

		for (LoggingProvider provider : ServiceLoader.load(LoggingProvider.class)) {
			providers.add(provider);
		}

		switch (providers.size()) {
			case 0:
				InternalLogger.log(Level.WARNING, WARNING_MESSAGE);
				return new NopLoggingProvider();
			case 1:
				return providers.get(0);
			default:
				return new WrapperLoggingProvider(providers);
		}
	}

	/**
	 * Loads the actual context provider.
	 *
	 * @return New context provider instance
	 */
	private static ContextProvider loadContextProvider() {
		List<ContextProvider> providers = new ArrayList<ContextProvider>(1);

		for (ContextProvider provider : ServiceLoader.load(ContextProvider.class)) {
			providers.add(provider);
		}

		switch (providers.size()) {
			case 0:
				return new NopContextProvider();
			case 1:
				return providers.get(0);
			default:
				return new WrapperContextProvider(providers);
		}
	}

}
