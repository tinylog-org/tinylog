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
 * Builder for creating {@link LoggingProvider LoggingProviders}.
 *
 * <p>
 *     This interface must be implemented by all logging backends and provided as
 *     {@link java.util.ServiceLoader service} in {@code META-INF/services}.
 * </p>
 */
public interface LoggingProviderBuilder {

	/**
	 * Gets the name of the logging backend, which can be used to address the logging backend in a configuration.
	 *
	 * <p>
	 *     The name must start with a lower case ASCII letter [a-z] and end with a lower case ASCII letter [a-z] or
	 *     digit [0-9]. Within the name, lower case letters [a-z], numbers [0-9], spaces [ ], and hyphens [-] are
	 *     allowed.
	 * </p>
	 *
	 * @return The name of the logging backend
	 */
	String getName();

	/**
	 * Creates a new instance of the logging backend.
	 *
	 * @param framework Configuration and hooks
	 * @return New instance of the logging backend
	 */
	LoggingProvider create(Framework framework);

}
