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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.tinylog.Level;
import org.tinylog.format.MessageFormatter;

/**
 * Logging provider that combines multiple logging providers into one.
 */
final class BundleLoggingProvider implements LoggingProvider {

	private final LoggingProvider[] loggingProviders;
	private final ContextProvider contextProvider;

	/**
	 * @param providers
	 *            Base logging providers
	 */
	BundleLoggingProvider(final Collection<LoggingProvider> providers) {
		loggingProviders = providers.toArray(new LoggingProvider[0]);
		contextProvider = createContextProvider(providers);
	}

	@Override
	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	@Override
	public Level getMinimumLevel() {
		Level minimumLevel = Level.OFF;
		for (int i = 0; i < loggingProviders.length; ++i) {
			Level level = loggingProviders[i].getMinimumLevel();
			if (level.ordinal() < minimumLevel.ordinal()) {
				minimumLevel = level;
			}
		}
		return minimumLevel;
	}

	@Override
	public Level getMinimumLevel(final String tag) {
		Level minimumLevel = Level.OFF;
		for (int i = 0; i < loggingProviders.length; ++i) {
			Level level = loggingProviders[i].getMinimumLevel(tag);
			if (level.ordinal() < minimumLevel.ordinal()) {
				minimumLevel = level;
			}
		}
		return minimumLevel;
	}

	@Override
	public boolean isEnabled(final int depth, final String tag, final Level level) {
		for (int i = 0; i < loggingProviders.length; ++i) {
			if (loggingProviders[i].isEnabled(depth + 1, tag, level)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void log(final int depth, final String tag, final Level level, final Throwable exception, final MessageFormatter formatter,
		final Object obj, final Object... arguments) {
		for (int i = 0; i < loggingProviders.length; ++i) {
			loggingProviders[i].log(depth + 1, tag, level, exception, formatter, obj, arguments);
		}
	}

	@Override
	public void log(final String loggerClassName, final String tag, final Level level, final Throwable exception,
		final MessageFormatter formatter, final Object obj, final Object... arguments) {
		for (int i = 0; i < loggingProviders.length; ++i) {
			loggingProviders[i].log(loggerClassName, tag, level, exception, formatter, obj, arguments);
		}
	}

	@Override
	public void shutdown() throws InterruptedException {
		for (int i = 0; i < loggingProviders.length; ++i) {
			loggingProviders[i].shutdown();
		}
	}

	/**
	 * Gets all context providers from given logging providers and combine them into a new one.
	 *
	 * @param loggingProviders
	 *            Context providers of these logging providers will be fetched
	 * @return All context providers combined into a new one
	 */
	private static ContextProvider createContextProvider(final Collection<LoggingProvider> loggingProviders) {
		Collection<ContextProvider> contextProviders = new ArrayList<ContextProvider>(loggingProviders.size());
		for (LoggingProvider loggingProvider : loggingProviders) {
			contextProviders.add(loggingProvider.getContextProvider());
		}
		return new BundleContextProvider(contextProviders);
	}

	
	/**
	 * Get all logging providers stored inside this bundle.
	 * @return All logging providers.
	 */
	List<LoggingProvider> getLoggingProviders() {
		return Arrays.asList(loggingProviders);		
	}
}
