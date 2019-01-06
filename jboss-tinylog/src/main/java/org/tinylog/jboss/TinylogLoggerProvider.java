/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.jboss;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.jboss.logging.Logger;
import org.jboss.logging.LoggerProvider;
import org.tinylog.ThreadContext;

/**
 * Logger provider for JBoss Logging that uses tinylog as back-end.
 */
public final class TinylogLoggerProvider implements LoggerProvider {
	
	private final ConcurrentMap<String, TinylogLogger> loggers;

	/** */
	public TinylogLoggerProvider() {
		loggers = new ConcurrentHashMap<String, TinylogLogger>();
	}

	@Override
	public Logger getLogger(final String name) {
		TinylogLogger logger = loggers.get(name);
		if (logger == null) {
			TinylogLogger newLogger = new TinylogLogger(name);
			TinylogLogger existingLogger = loggers.putIfAbsent(name, newLogger);
			return existingLogger == null ? newLogger : existingLogger;
		} else {
			return logger;
		}
	}

	@Override
	public void clearMdc() {
		ThreadContext.clear();
	}

	@Override
	public Object putMdc(final String key, final Object value) {
		ThreadContext.put(key, value);
		return null;
	}

	@Override
	public Object getMdc(final String key) {
		return ThreadContext.get(key);
	}

	@Override
	public void removeMdc(final String key) {
		ThreadContext.remove(key);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String, Object> getMdcMap() {
		return (Map) ThreadContext.getMapping();
	}

	@Override
	public void clearNdc() {
		// Ignore
	}

	@Override
	public String getNdc() {
		return null;
	}

	@Override
	public int getNdcDepth() {
		return 0;
	}

	@Override
	public String popNdc() {
		return "";
	}

	@Override
	public String peekNdc() {
		return "";
	}

	@Override
	public void pushNdc(final String message) {
		// Ignore
	}

	@Override
	public void setNdcMaxDepth(final int maxDepth) {
		// Ignore
	}

}
