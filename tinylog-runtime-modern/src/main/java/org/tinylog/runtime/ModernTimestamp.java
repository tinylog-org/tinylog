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

package org.tinylog.runtime;

import java.time.Instant;

/**
 * Modern timestamp based on {@link Instant} with nanosecond precision.
 */
public final class ModernTimestamp implements Timestamp<Instant> {

	private final Instant instant;

	/**
	 * @param instant Instant to store as timestamp
	 */
	ModernTimestamp(Instant instant) {
		this.instant = instant;
	}

	@Override
	public Instant resole() {
		return instant;
	}

}
