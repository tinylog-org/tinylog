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

import java.util.Collection;

import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.configuration.ServiceLoader;

/**
 * Registry for receiving the actual logging provider.
 *
 * <p>
 * As service registered logging providers will be loaded from <tt>META-INF/services</tt>. If there are multiple logging
 * providers, they will be combined to one.
 * </p>
 */
public final class ProviderRegistry {

	private static final String PROVIDER_PROPERTY = "provider";

	private static final LoggingProvider loggingProvider = loadLoggingProvider();

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
		return loggingProvider;
	}

	/**
	 * Loads the actual logging provider.
	 *
	 * @return New logging provider instance
	 */
	private static LoggingProvider loadLoggingProvider() {
		ServiceLoader<LoggingProvider> loader = new ServiceLoader<LoggingProvider>(LoggingProvider.class);
		String name = Configuration.get(PROVIDER_PROPERTY);

		if (name == null) {
			Collection<LoggingProvider> providers = loader.createAll();
			switch (providers.size()) {
				case 0:
					InternalLogger.log(Level.WARNING, "No logging framework implementation found in class path."
						+ "Add tinylog-impl.jar for outputting log entries.");
					return new NopLoggingProvider();
				case 1:
					return providers.iterator().next();
				default:
					return new BundleLoggingProvider(providers);
			}
		} else {
			LoggingProvider provider = loader.create(name);
			if (provider == null) {
				InternalLogger.log(Level.ERROR, "Requested logging provider is not available. Logging will be disabled.");
				return new NopLoggingProvider();
			} else {
				return provider;
			}
		}
	}

}
