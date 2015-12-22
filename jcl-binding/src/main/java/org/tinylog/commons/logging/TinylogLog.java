/*
 * Copyright 2015 Martin Winandy
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

package org.tinylog.commons.logging;

import org.apache.commons.logging.Log;
import org.tinylog.Level;

/**
 * Apache Commons Logging (JCL) compatible logger implementation.
 */
public final class TinylogLog implements Log {

	/**
	 * @param name
	 *            Name of logger (will be ignored)
	 */
	public TinylogLog(final String name) {
	}

	@Override
	public boolean isTraceEnabled() {
		return TinylogBridge.isEnabled(Level.TRACE);
	}

	@Override
	public void trace(final Object message) {
		TinylogBridge.log(Level.TRACE, message);
	}

	@Override
	public void trace(final Object message, final Throwable exception) {
		TinylogBridge.log(Level.TRACE, message == exception ? null : message, exception);
	}

	@Override
	public boolean isDebugEnabled() {
		return TinylogBridge.isEnabled(Level.DEBUG);
	}

	@Override
	public void debug(final Object message) {
		TinylogBridge.log(Level.DEBUG, message);
	}

	@Override
	public void debug(final Object message, final Throwable exception) {
		TinylogBridge.log(Level.DEBUG, message == exception ? null : message, exception);
	}

	@Override
	public boolean isInfoEnabled() {
		return TinylogBridge.isEnabled(Level.INFO);
	}

	@Override
	public void info(final Object message) {
		TinylogBridge.log(Level.INFO, message);
	}

	@Override
	public void info(final Object message, final Throwable exception) {
		TinylogBridge.log(Level.INFO, message == exception ? null : message, exception);
	}

	@Override
	public boolean isWarnEnabled() {
		return TinylogBridge.isEnabled(Level.WARNING);
	}

	@Override
	public void warn(final Object message) {
		TinylogBridge.log(Level.WARNING, message);
	}

	@Override
	public void warn(final Object message, final Throwable exception) {
		TinylogBridge.log(Level.WARNING, message == exception ? null : message, exception);
	}

	@Override
	public boolean isErrorEnabled() {
		return TinylogBridge.isEnabled(Level.ERROR);
	}

	@Override
	public void error(final Object message) {
		TinylogBridge.log(Level.ERROR, message);
	}

	@Override
	public void error(final Object message, final Throwable exception) {
		TinylogBridge.log(Level.ERROR, message == exception ? null : message, exception);
	}

	@Override
	public boolean isFatalEnabled() {
		return TinylogBridge.isEnabled(Level.ERROR);
	}

	@Override
	public void fatal(final Object message) {
		TinylogBridge.log(Level.ERROR, message);
	}

	@Override
	public void fatal(final Object message, final Throwable exception) {
		TinylogBridge.log(Level.ERROR, message == exception ? null : message, exception);
	}

}
