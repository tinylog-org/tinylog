/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.core;

import java.util.Locale;

import org.tinylog.configuration.Configuration;

/**
 * Parser for properties based configuration.
 *
 * @see Configuration
 */
public final class ConfigurationParser {

	private static final int MAX_LOCALE_ARGUMENTS = 3;

	/** */
	private ConfigurationParser() {
	}

	/**
	 * Loads the locale from configuration.
	 *
	 * @return Locale from configuration or {@link Locale#ROOT} if no locale is configured
	 */
	public static Locale getLocale() {
		String tag = Configuration.get("locale");
		if (tag == null) {
			return Locale.ROOT;
		} else {
			String[] splittedTag = tag.split("_", MAX_LOCALE_ARGUMENTS);
			if (splittedTag.length == 1) {
				return new Locale(splittedTag[0]);
			} else if (splittedTag.length == 2) {
				return new Locale(splittedTag[0], splittedTag[1]);
			} else {
				return new Locale(splittedTag[0], splittedTag[1], splittedTag[2]);
			}
		}
	}

}
