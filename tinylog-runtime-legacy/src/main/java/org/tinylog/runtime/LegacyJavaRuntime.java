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

package org.tinylog.runtime;

import java.util.Date;
import java.util.Locale;

/**
 * Runtime implementation for legacy Java 7 and 8.
 */
public final class LegacyJavaRuntime implements RuntimeFlavor<Date> {

	/** */
	public LegacyJavaRuntime() {
	}

	@Override
	public LegacyTimestamp createTimestamp() {
		return new LegacyTimestamp(new Date());
	}

	@Override
	public LegacyTimestampFormatter createTimestampFormatter(String pattern, Locale locale) {
		return new LegacyTimestampFormatter(pattern, locale);
	}

}
