/*
 * Copyright 2012 Martin Winandy
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

package org.apache.log4j;

import java.net.URL;
import java.util.Properties;

/**
 * Empty property configurator skeleton (use tinylog for real configuration).
 */
public class PropertyConfigurator {

	/** */
	public PropertyConfigurator() {
	}

	/**
	 * Do nothing (this method exists only for compatible reasons).
	 */
	public static void configure(final Properties properties) {
		// Do nothing
	}

	/**
	 * Do nothing (this method exists only for compatible reasons).
	 */
	public static void configure(final String configFilename) {
		// Do nothing
	}

	/**
	 * Do nothing (this method exists only for compatible reasons).
	 */
	public static void configure(final URL configURL) {
		// Do nothing
	}

	/**
	 * Do nothing (this method exists only for compatible reasons).
	 */
	public static void configureAndWatch(final String configFilename) {
		// Do nothing
	}

	/**
	 * Do nothing (this method exists only for compatible reasons).
	 */
	public static void configureAndWatch(final String configFilename, final long delay) {
		// Do nothing
	}

}
