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

import org.tinylog.policies.DynamicNamePolicy;
import org.tinylog.policies.Policy;
import org.tinylog.runtime.Timestamp;

/**
 * Path segment that represents a dynamic text.
 */
public class DynamicNameSegment implements Segment {

	private static String dynamicName;

	DynamicNameSegment(final String defaultValue) {
		setDynamicName(defaultValue);
	}

	/**
	 * Returns the current dynamic name.
	 *
	 * @return Dynamic name
	 */
	public static String getDynamicName() {
		return dynamicName;
	}

	/**
	 * Sets a new dynamic name.
	 *
	 * <p>When used together with {@link DynamicNamePolicy} and the dynamic name differs from the current one,
	 * a {@linkplain Policy#reset() reset} is triggered.</p>
	 *
	 * @param newDynamicName Dynamic name to set
	 */
	public static void setDynamicName(final String newDynamicName) {
		if (dynamicName != null && dynamicName.equals(newDynamicName)) {
			return;
		}
		dynamicName = newDynamicName;
		DynamicNamePolicy.setReset();
	}

	@Override
	public String getStaticText() {
		return getDynamicName();
	}

	@Override
	public boolean validateToken(final String token) {
		return dynamicName != null && dynamicName.equals(token);
	}

	@Override
	public String createToken(final String prefix, final Timestamp timestamp) {
		return getDynamicName();
	}
}
