/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;

/**
 * Logging factory implementation for providing {@link LegacyTinylogLogger} instances.
 */
public final class LegacyTinylogLoggerFactory implements ILoggerFactory {

	private final ConcurrentMap<String, LegacyTinylogLogger> loggers;

	/** */
	public LegacyTinylogLoggerFactory() {
		loggers = new ConcurrentHashMap<String, LegacyTinylogLogger>();
	}

	@Override
	public LegacyTinylogLogger getLogger(final String name) {
		LegacyTinylogLogger logger = loggers.get(name);
		if (logger == null) {
			LegacyTinylogLogger newLogger = new LegacyTinylogLogger(name);
			LegacyTinylogLogger existingLogger = loggers.putIfAbsent(name, newLogger);
			return existingLogger == null ? newLogger : existingLogger;
		} else {
			return logger;
		}
	}

}
