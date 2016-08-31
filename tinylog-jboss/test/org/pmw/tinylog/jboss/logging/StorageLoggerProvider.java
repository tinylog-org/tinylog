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

package org.pmw.tinylog.jboss.logging;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.LoggerProvider;

/**
 * Logger provider implementation for JBoss Logging.
 */
public final class StorageLoggerProvider implements LoggerProvider {
	
	private static final Map<String, Object> mdc = new HashMap<>();
	private static final Map<String, StorageLogger> loggers = new HashMap<>();
	
	/** */
	public StorageLoggerProvider() {
	}
	
	/**
	 * Get all created loggers.
	 * 
	 * @return Created loggers
	 */
	public static Iterable<StorageLogger> getLoggers() {
		return loggers.values();
	}

	@Override
	public StorageLogger getLogger(final String name) {
		StorageLogger logger = loggers.get(name);
		if (logger == null) {
			logger = new StorageLogger(getParentLogger(name), name);
			loggers.put(name, logger);
		}
		return logger;
	}

	@Override
	public void clearMdc() {
		mdc.clear();
	}

	@Override
	public Object putMdc(final String key, final Object value) {
		return mdc.put(key, value);
	}

	@Override
	public Object getMdc(final String key) {
		return mdc.get(key);
	}

	@Override
	public void removeMdc(final String key) {
		mdc.remove(key);
	}

	@Override
	public Map<String, Object> getMdcMap() {
		return mdc;
	}

	@Override
	public void clearNdc() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNdc() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getNdcDepth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String popNdc() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String peekNdc() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void pushNdc(final String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setNdcMaxDepth(final int maxDepth) {
		throw new UnsupportedOperationException();
	}

	private StorageLogger getParentLogger(final String name) {
		if (name.isEmpty()) {
			return null;
		} else {
			int index = name.lastIndexOf('.');
			if (index == -1) {
				return getLogger("");
			} else {
				return getLogger(name.substring(0, index));
			}
		}
	}

}
