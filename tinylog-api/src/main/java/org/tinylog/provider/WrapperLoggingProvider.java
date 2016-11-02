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

/**
 * Logging provider that combines multiple logging providers into one.
 */
final class WrapperLoggingProvider implements LoggingProvider {

	private final LoggingProvider[] providers;

	/**
	 * @param providers
	 *            Base logging providers
	 */
	WrapperLoggingProvider(final Collection<LoggingProvider> providers) {
		this.providers = providers.toArray(new LoggingProvider[providers.size()]);
	}

	@Override
	public Level getMinimumLevel(final String tag) {
		Level minimumLevel = Level.OFF;
		for (int i = 0; i < providers.length; ++i) {
			Level level = providers[i].getMinimumLevel(tag);
			if (level.ordinal() < minimumLevel.ordinal()) {
				minimumLevel = level;
			}
		}
		return minimumLevel;
	}

	@Override
	public boolean isEnabled(final int depth, final String tag, final Level level) {
		for (int i = 0; i < providers.length; ++i) {
			if (providers[i].isEnabled(depth + 1, tag, level)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void log(final int depth, final String tag, final Level level, final Throwable exception, final Object obj,
		final Object... arguments) {
		for (int i = 0; i < providers.length; ++i) {
			providers[i].log(depth + 1, tag, level, exception, obj, arguments);
		}
	}

	@Override
	public void internal(final int depth, final Level level, final Throwable exception, final String message) {
		for (int i = 0; i < providers.length; ++i) {
			providers[i].internal(depth + 1, level, exception, message);
		}
	}

}
