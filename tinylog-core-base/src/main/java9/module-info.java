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
import org.tinylog.core.format.value.DateFormatBuilder;
import org.tinylog.core.format.value.JavaTimeFormatBuilder;
import org.tinylog.core.format.value.NumberFormatBuilder;
import org.tinylog.core.format.value.ValueFormatBuilder;
import org.tinylog.core.backend.LoggingBackendBuilder;
import org.tinylog.core.backend.InternalLoggingBackendBuilder;
import org.tinylog.core.backend.NopLoggingBackendBuilder;

module org.tinylog.core {
	uses Hook;

	uses ValueFormatBuilder;
	provides ValueFormatBuilder with
		DateFormatBuilder,
		JavaTimeFormatBuilder,
		NumberFormatBuilder;

	uses LoggingBackendBuilder;
	provides LoggingBackendBuilder with
		InternalLoggingBackendBuilder,
		NopLoggingBackendBuilder;

	exports org.tinylog.core;
	exports org.tinylog.core.format.message;
	exports org.tinylog.core.format.value;
	exports org.tinylog.core.backend;
	exports org.tinylog.core.runtime;
}
