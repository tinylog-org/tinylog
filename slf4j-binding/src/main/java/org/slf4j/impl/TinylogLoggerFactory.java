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

package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.ILoggerFactory;

/**
 * Factory for {@link TinylogLogger}.
 */
public final class TinylogLoggerFactory implements ILoggerFactory {

	private final ConcurrentMap<String, TinylogLogger> loggers;

	/** */
	public TinylogLoggerFactory() {
		loggers = new ConcurrentHashMap<>();
	}

	@Override
	public TinylogLogger getLogger(final String name) {
		TinylogLogger logger = loggers.get(name);
		if (logger == null) {
			TinylogLogger newLogger = new TinylogLogger(name);
			TinylogLogger existingLogger = loggers.putIfAbsent(name, newLogger);
			return existingLogger == null ? newLogger : existingLogger;
		} else {
			return logger;
		}
	}

}
