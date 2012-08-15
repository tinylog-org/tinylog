/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog.util;

import mockit.Mock;
import mockit.MockClass;

/**
 * Mock for {@link System}.
 */
@MockClass(realClass = System.class)
public final class MockSystem {

	private long time;

	/** */
	public MockSystem() {
		time = 0L;
	}

	/**
	 * Get the current time in milliseconds.
	 * 
	 * @return Current time in milliseconds
	 */
	@Mock
	public long currentTimeMillis() {
		return time;
	}

	/**
	 * Set the current time.
	 * 
	 * @param time
	 *            Current time in milliseconds
	 */
	public void setCurrentTimeMillis(final long time) {
		this.time = time;
	}

}
