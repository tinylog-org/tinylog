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

package org.tinylog.core.providers;

import org.tinylog.core.Framework;

/**
 * Builder for {@link NopLoggingProvider}.
 */
public class NopLoggingProviderBuilder implements LoggingProviderBuilder {

	/** */
	public NopLoggingProviderBuilder() {
	}

	@Override
	public String getName() {
		return "nop";
	}

	@Override
	public LoggingProvider create(Framework framework) {
		return new NopLoggingProvider();
	}

}
