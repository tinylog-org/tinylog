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

package org.tinylog.runtime;

/**
 * Formatter for {@link Timestamp Timestamps}.
 */
public interface TimestampFormatter {

	/**
	 * Checks whether a formatted timestamp is compatible with the pattern of this formatter.
	 *
	 * @param timestamp
	 *            Formatted timestamp
	 * @return {@code true} if the given timestamp is compatible with the pattern of this formatter, {@code false} if
	 *         not
	 */
	boolean isValid(String timestamp);

	/**
	 * Formats a timestamp.
	 *
	 * @param timestamp
	 *            Timestamp to format
	 * @return Formatted timestamp
	 */
	String format(Timestamp timestamp);

}
