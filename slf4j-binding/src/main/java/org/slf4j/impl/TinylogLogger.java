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

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.tinylog.Level;

/**
 * SLF4J compatible logger implementation.
 */
public final class TinylogLogger implements Logger {

	private final String name;

	/**
	 * @param name
	 *            Name of the logger
	 */
	public TinylogLogger(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isTraceEnabled() {
		return TinylogBridge.isEnabled(Level.TRACE);
	}

	@Override
	public boolean isTraceEnabled(final Marker marker) {
		return TinylogBridge.isEnabled(Level.TRACE);
	}

	@Override
	public void trace(final String message) {
		TinylogBridge.log(Level.TRACE, message);
	}

	@Override
	public void trace(final Marker marker, final String message) {
		TinylogBridge.log(Level.TRACE, message);
	}

	@Override
	public void trace(final String message, final Object argument) {
		TinylogBridge.log(Level.TRACE, message, argument);
	}

	@Override
	public void trace(final Marker marker, final String message, final Object argument) {
		TinylogBridge.log(Level.TRACE, message, argument);
	}

	@Override
	public void trace(final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.TRACE, message, argument1, argument2);
	}

	@Override
	public void trace(final Marker marker, final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.TRACE, message, argument1, argument2);
	}

	@Override
	public void trace(final String message, final Object... arguments) {
		TinylogBridge.log(Level.TRACE, message, arguments);
	}

	@Override
	public void trace(final Marker marker, final String message, final Object... arguments) {
		TinylogBridge.log(Level.TRACE, message, arguments);
	}

	@Override
	public void trace(final String message, final Throwable throwable) {
		TinylogBridge.log(Level.TRACE, message, throwable);
	}

	@Override
	public void trace(final Marker marker, final String message, final Throwable throwable) {
		TinylogBridge.log(Level.TRACE, message, throwable);
	}

	@Override
	public boolean isDebugEnabled() {
		return TinylogBridge.isEnabled(Level.DEBUG);
	}

	@Override
	public boolean isDebugEnabled(final Marker marker) {
		return TinylogBridge.isEnabled(Level.DEBUG);
	}

	@Override
	public void debug(final String message) {
		TinylogBridge.log(Level.DEBUG, message);
	}

	@Override
	public void debug(final Marker marker, final String message) {
		TinylogBridge.log(Level.DEBUG, message);
	}

	@Override
	public void debug(final String message, final Object argument) {
		TinylogBridge.log(Level.DEBUG, message, argument);
	}

	@Override
	public void debug(final Marker marker, final String message, final Object argument) {
		TinylogBridge.log(Level.DEBUG, message, argument);
	}

	@Override
	public void debug(final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.DEBUG, message, argument1, argument2);
	}

	@Override
	public void debug(final Marker marker, final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.DEBUG, message, argument1, argument2);
	}

	@Override
	public void debug(final String message, final Object... arguments) {
		TinylogBridge.log(Level.DEBUG, message, arguments);
	}

	@Override
	public void debug(final Marker marker, final String message, final Object... arguments) {
		TinylogBridge.log(Level.DEBUG, message, arguments);
	}

	@Override
	public void debug(final String message, final Throwable throwable) {
		TinylogBridge.log(Level.DEBUG, message, throwable);
	}

	@Override
	public void debug(final Marker marker, final String message, final Throwable throwable) {
		TinylogBridge.log(Level.DEBUG, message, throwable);
	}

	@Override
	public boolean isInfoEnabled() {
		return TinylogBridge.isEnabled(Level.INFO);
	}

	@Override
	public boolean isInfoEnabled(final Marker marker) {
		return TinylogBridge.isEnabled(Level.INFO);
	}

	@Override
	public void info(final String message) {
		TinylogBridge.log(Level.INFO, message);
	}

	@Override
	public void info(final Marker marker, final String message) {
		TinylogBridge.log(Level.INFO, message);
	}

	@Override
	public void info(final String message, final Object argument) {
		TinylogBridge.log(Level.INFO, message, argument);
	}

	@Override
	public void info(final Marker marker, final String message, final Object argument) {
		TinylogBridge.log(Level.INFO, message, argument);
	}

	@Override
	public void info(final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.INFO, message, argument1, argument2);
	}

	@Override
	public void info(final Marker marker, final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.INFO, message, argument1, argument2);
	}

	@Override
	public void info(final String message, final Object... arguments) {
		TinylogBridge.log(Level.INFO, message, arguments);
	}

	@Override
	public void info(final Marker marker, final String message, final Object... arguments) {
		TinylogBridge.log(Level.INFO, message, arguments);
	}

	@Override
	public void info(final String message, final Throwable throwable) {
		TinylogBridge.log(Level.INFO, message, throwable);
	}

	@Override
	public void info(final Marker marker, final String message, final Throwable throwable) {
		TinylogBridge.log(Level.INFO, message, throwable);
	}

	@Override
	public boolean isWarnEnabled() {
		return TinylogBridge.isEnabled(Level.WARNING);
	}

	@Override
	public boolean isWarnEnabled(final Marker marker) {
		return TinylogBridge.isEnabled(Level.WARNING);
	}

	@Override
	public void warn(final String message) {
		TinylogBridge.log(Level.WARNING, message);
	}

	@Override
	public void warn(final Marker marker, final String message) {
		TinylogBridge.log(Level.WARNING, message);
	}

	@Override
	public void warn(final String message, final Object argument) {
		TinylogBridge.log(Level.WARNING, message, argument);
	}

	@Override
	public void warn(final Marker marker, final String message, final Object argument) {
		TinylogBridge.log(Level.WARNING, message, argument);
	}

	@Override
	public void warn(final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.WARNING, message, argument1, argument2);
	}

	@Override
	public void warn(final Marker marker, final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.WARNING, message, argument1, argument2);
	}

	@Override
	public void warn(final String message, final Object... arguments) {
		TinylogBridge.log(Level.WARNING, message, arguments);
	}

	@Override
	public void warn(final Marker marker, final String message, final Object... arguments) {
		TinylogBridge.log(Level.WARNING, message, arguments);
	}

	@Override
	public void warn(final String message, final Throwable throwable) {
		TinylogBridge.log(Level.WARNING, message, throwable);
	}

	@Override
	public void warn(final Marker marker, final String message, final Throwable throwable) {
		TinylogBridge.log(Level.WARNING, message, throwable);
	}

	@Override
	public boolean isErrorEnabled() {
		return TinylogBridge.isEnabled(Level.ERROR);
	}

	@Override
	public boolean isErrorEnabled(final Marker marker) {
		return TinylogBridge.isEnabled(Level.ERROR);
	}

	@Override
	public void error(final String message) {
		TinylogBridge.log(Level.ERROR, message);
	}

	@Override
	public void error(final Marker marker, final String message) {
		TinylogBridge.log(Level.ERROR, message);
	}

	@Override
	public void error(final String message, final Object argument) {
		TinylogBridge.log(Level.ERROR, message, argument);
	}

	@Override
	public void error(final Marker marker, final String message, final Object argument) {
		TinylogBridge.log(Level.ERROR, message, argument);
	}

	@Override
	public void error(final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.ERROR, message, argument1, argument2);
	}

	@Override
	public void error(final Marker marker, final String message, final Object argument1, final Object argument2) {
		TinylogBridge.log(Level.ERROR, message, argument1, argument2);
	}

	@Override
	public void error(final String message, final Object... arguments) {
		TinylogBridge.log(Level.ERROR, message, arguments);
	}

	@Override
	public void error(final Marker marker, final String message, final Object... arguments) {
		TinylogBridge.log(Level.ERROR, message, arguments);
	}

	@Override
	public void error(final String message, final Throwable throwable) {
		TinylogBridge.log(Level.ERROR, message, throwable);
	}

	@Override
	public void error(final Marker marker, final String message, final Throwable throwable) {
		TinylogBridge.log(Level.ERROR, message, throwable);
	}

}
