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
 * Path segment that represents plain static text.
 */
final class PlainTextSegment implements Segment {

	private final String text;

	/**
	 * @param text
	 *            Plain static text
	 */
	PlainTextSegment(final String text) {
		this.text = text;
	}

	@Override
	public String getStaticText() {
		return text;
	}

	@Override
	public boolean validateToken(final String token) {
		return text.equals(token);
	}

	@Override
	public String createToken(final String prefix, final Timestamp timestamp) {
		return text;
	}

}
