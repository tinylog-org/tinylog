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

import org.tinylog.core.Hook;
import org.tinylog.core.formats.DateFormatBuilder;
import org.tinylog.core.formats.JavaTimeFormatBuilder;
import org.tinylog.core.formats.NumberFormatBuilder;
import org.tinylog.core.formats.ValueFormatBuilder;
import org.tinylog.core.providers.LoggingProviderBuilder;
import org.tinylog.core.providers.NopLoggingProviderBuilder;

module org.tinylog.core {
	uses Hook;

	uses ValueFormatBuilder;
	provides ValueFormatBuilder with DateFormatBuilder, JavaTimeFormatBuilder, NumberFormatBuilder;

	uses LoggingProviderBuilder;
	provides LoggingProviderBuilder with NopLoggingProviderBuilder;

	exports org.tinylog.core;
	exports org.tinylog.core.formats;
	exports org.tinylog.core.formatters;
	exports org.tinylog.core.providers;
	exports org.tinylog.runtime;
}
