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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.tinylog.Level;
import org.tinylog.configuration.Configuration;
import org.tinylog.configuration.ServiceLoader;
import org.tinylog.runtime.RuntimeProvider;

/**
 * Registry for receiving the actual logging provider.
 *
 * <p>
 * As service registered logging providers will be loaded from {@code META-INF/services}. If there are multiple logging
 * providers, they will be combined to one.
 * </p>
 */
public final class ProviderRegistry {

	private static final String PROVIDER_PROPERTY = "provider";
	private static final String NOP_PROVIDER_NAME = "nop";

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
	 * Gets all loaded logging providers.  
	 * 
	 * <p>
	 * If the logging provider is a {@link BundleLoggingProvider} resolve its contents and return them.
	 * </p>
	 * 
	 * @return The list of all logging providers.
	 */
	public static List<LoggingProvider> getLoggingProviders() {
		if (loggingProvider instanceof BundleLoggingProvider) {
			return ((BundleLoggingProvider) loggingProvider).getLoggingProviders();
		} else {
			return Collections.singletonList(loggingProvider);
		}
	}

	/**
	 * Loads the actual logging provider.
	 *
	 * @return New logging provider instance
	 */
	private static LoggingProvider loadLoggingProvider() {
		if (RuntimeProvider.getProcessId() == Long.MIN_VALUE) {
			java.util.ServiceLoader.load(LoggingProvider.class); // Workaround for ProGuard (see issue #126)
		}

		ServiceLoader<LoggingProvider> loader = new ServiceLoader<LoggingProvider>(LoggingProvider.class);
		String name = Configuration.get(PROVIDER_PROPERTY);

		if (name == null) {
			Collection<LoggingProvider> providers = loader.createAll();
			switch (providers.size()) {
				case 0:
					InternalLogger.log(Level.WARN, "No logging framework implementation found in classpath."
						+ " Add tinylog-impl.jar for outputting log entries.");
					return new NopLoggingProvider();
				case 1:
					return providers.iterator().next();
				default:
					return new BundleLoggingProvider(providers);
			}
		} else if (NOP_PROVIDER_NAME.equalsIgnoreCase(name)) {
			return new NopLoggingProvider();
		} else {
			String[] nameItems = name.trim().split(",");
			Collection<LoggingProvider> providers = new ArrayList<LoggingProvider>(nameItems.length);
			for (String nameItem : nameItems) {
				nameItem = nameItem.trim();
				if (nameItem.isEmpty()) {
					InternalLogger.log(Level.WARN, "Requested logging provider 'empty string' will be ignored.");
					continue;
				}
				LoggingProvider provider = loader.create(nameItem);
				if (provider == null) {
					InternalLogger.log(Level.ERROR, "Requested logging provider '" + nameItem + "' is not available.");
				} else {
					providers.add(provider);	
				}
			}

			if (providers.size() == 0) {
				InternalLogger.log(Level.ERROR, "Requested logging provider '" + name + "' is not available. Logging will be disabled.");
				return new NopLoggingProvider();
			} else if (providers.size() == 1) {
				return providers.iterator().next();
			} else {
				return new BundleLoggingProvider(providers);
			}
		}
	}

}
