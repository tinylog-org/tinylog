/*
 * Copyright 2021 Martin Winandy
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

import org.tinylog.policies.DynamicPolicy;
import org.tinylog.runtime.Timestamp;

/**
 * Path segment that represents a dynamic text.
 */
public class DynamicSegment implements Segment {

	private static final Object mutex = new Object();

	private static boolean created;
	private static String text;

	/**
	 * @param defaultValue Initial value for dynamic text
	 */
	DynamicSegment(final String defaultValue) {
		synchronized (mutex) {
			if (text == null) {
				text = defaultValue;
			}
		}
	}

	/**
	 * Returns the current dynamic text.
	 *
	 * @return Dynamic text
	 */
	public static String getText() {
		synchronized (mutex) {
			return text;
		}
	}

	/**
	 * Sets a new dynamic text.
	 *
	 * <p>
	 *     When used together with {@link DynamicPolicy} and the dynamic text differs from the current one,
	 *     a {@linkplain DynamicPolicy#reset() reset} is triggered.
	 * </p>
	 *
	 * @param text Dynamic text to set
	 */
	public static void setText(final String text) {
		synchronized (mutex) {
			if (DynamicSegment.text != null && DynamicSegment.text.equals(text)) {
				return;
			}

			DynamicSegment.text = text;

			if (created) {
				DynamicPolicy.setReset();
			}
		}
	}

	@Override
	public String getStaticText() {
		synchronized (mutex) {
			created = true;
			return text;
		}
	}

	@Override
	public boolean validateToken(final String token) {
		synchronized (mutex) {
			return text != null && text.equals(token);
		}
	}

	@Override
	public String createToken(final String prefix, final Timestamp timestamp) {
		return getStaticText();
	}

}
