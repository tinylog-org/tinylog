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

import org.slf4j.spi.LoggerFactoryBinder;

/**
 * The binding for {@link TinylogLoggerFactory}.
 */
public final class StaticLoggerBinder implements LoggerFactoryBinder {

	/**
	 * Declare the version of the SLF4J API this implementation is compiled against. The value of this field is usually
	 * modified with each release.
	 */
	// To avoid constant folding by the compiler, this field must NOT be final
	public static String REQUESTED_API_VERSION = "1.6.99"; // SUPPRESS CHECKSTYLE

	private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();
	private static final String loggerFactoryClassName = TinylogLoggerFactory.class.getName();

	private final TinylogLoggerFactory loggerFactory;

	private StaticLoggerBinder() {
		loggerFactory = new TinylogLoggerFactory();
	}

	/**
	 * Get the unique instance of static logger binder.
	 *
	 * @return Instance of static logger binder.
	 */
	public static StaticLoggerBinder getSingleton() {
		return INSTANCE;
	}

	@Override
	public TinylogLoggerFactory getLoggerFactory() {
		return loggerFactory;
	}

	@Override
	public String getLoggerFactoryClassStr() {
		return loggerFactoryClassName;
	}

}
