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

package org.tinylog.core.format.value;

import java.time.ZoneId;
import java.util.Locale;

/**
 * Builder for creating {@link JavaTimeFormat TemporalAccessorFormats}.
 */
public final class JavaTimeFormatBuilder implements ValueFormatBuilder {

	/** */
	public JavaTimeFormatBuilder() {
	}

	@Override
	public JavaTimeFormat create(Locale locale) {
		return new JavaTimeFormat(locale, ZoneId.systemDefault());
	}

}
