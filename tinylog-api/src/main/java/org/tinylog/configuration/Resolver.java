/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.configuration;

/**
 * Resolver for any kind of placeholders.
 */
interface Resolver {

	/**
	 * Gets the name of the underlying data.
	 * 
	 * @return Name of underlying data
	 */
	String getName();

	/**
	 * Get prefix character for placeholders.
	 * 
	 * @return Prefix character
	 */
	char getPrefix();

	/**
	 * Resolves a placeholder with real data.
	 * 
	 * @param name
	 *            Name of placeholder
	 * @return Resolved data or {@code null}
	 */
	String resolve(String name);

}
