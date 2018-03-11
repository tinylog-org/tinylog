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

package org.tinylog.path;

import org.tinylog.runtime.Timestamp;

/**
 * Segment of a {@link org.tinylog.path.DynamicPath dynamic path} .
 *
 * <p>
 * A segment represents either a pattern or static text.
 * </p>
 */
interface Segment {

	/**
	 * Gets the static text if available.
	 *
	 * @return Static text or {@code null}
	 */
	String getStaticText();

	/**
	 * Verifies whether a token matches with the segment.
	 *
	 * @param token
	 *            Token to match
	 * @return {@code true} if token matches with the segment, {@code false} if not
	 */
	boolean validateToken(String token);

	/**
	 * Generates a new token.
	 *
	 * @param prefix
	 *            Already generated path
	 * @param timestamp
	 *            Timestamp for date and time representations
	 * @return Generated token
	 */
	String createToken(String prefix, Timestamp timestamp);

}
