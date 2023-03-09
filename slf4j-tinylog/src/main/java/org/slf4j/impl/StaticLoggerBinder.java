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

package org.slf4j.impl;

import org.slf4j.ILoggerFactory;
import org.slf4j.spi.LoggerFactoryBinder;
import org.tinylog.slf4j.LegacyTinylogLoggerFactory;

/**
 * Logger factory binder for using tinylog for logging via SLF4J.
 */
public final class StaticLoggerBinder implements LoggerFactoryBinder {

	/**
	 * SLF4J API 1.6 and newer is supported.
	 */
	// This field must not be final to avoid constant folding by the compiler.
	// @checkstyle off: StaticVariableName|VisibilityModifier
	public static String REQUESTED_API_VERSION = "1.6";
	// @checkstyle on: StaticVariableName|VisibilityModifier

	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	private final ILoggerFactory factory;

	/** */
	private StaticLoggerBinder() {
		factory = new LegacyTinylogLoggerFactory();
	}

	/**
	 * Gets the singleton instance of this static logger binder.
	 *
	 * @return Static logger binder instance
	 */
	public static StaticLoggerBinder getSingleton() {
		return SINGLETON;
	}

	@Override
	public ILoggerFactory getLoggerFactory() {
		return factory;
	}

	@Override
	public String getLoggerFactoryClassStr() {
		return factory.getClass().getName();
	}

}
