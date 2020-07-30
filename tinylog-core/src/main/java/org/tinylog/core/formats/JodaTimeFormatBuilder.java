/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.formats;

import java.util.Locale;

import org.joda.time.DateTimeZone;

/**
 * Builder for creating {@link JodaTimeFormat JodaTimeFormats}.
 */
public final class JodaTimeFormatBuilder implements ValueFormatBuilder {

	/** */
	public JodaTimeFormatBuilder() {
	}

	@Override
	public boolean isCompatible() {
		try {
			Class.forName("org.joda.time.format.DateTimeFormatter");
			return true;
		} catch (ClassNotFoundException ex) {
			return false;
		}
	}

	@Override
	public JodaTimeFormat create(Locale locale) {
		return new JodaTimeFormat(locale, DateTimeZone.getDefault());
	}

}
