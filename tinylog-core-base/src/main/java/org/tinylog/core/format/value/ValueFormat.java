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

/**
 * Format interface for different value types.
 */
public interface ValueFormat {

	/**
	 * Checks if the passed value is supported.
	 *
	 * @param value Value to test
	 * @return {@code true} if the passed value is supported, {@code false} if not
	 */
	boolean isSupported(Object value);

	/**
	 * Formats the passed value.
	 *
	 * @param pattern Format pattern for the value
	 * @param value Value to format
	 * @return Formatted value
	 */
	String format(String pattern, Object value);

}
