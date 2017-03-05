/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.assertions;

import java.lang.reflect.Method;

/**
 * Contains all assert methods for custom assertions that are not already supported by
 * {@link org.assertj.core.api.Assertions}.
 */
public final class Assertions {

	/** */
	private Assertions() {
	}

	/**
	 * Creates assertion for {@link Method}.
	 *
	 * @param actual
	 *            Method under test
	 * @return Assertion with test methods
	 */
	public static MethodAssert assertThat(final Method actual) {
		return new MethodAssert(actual);
	}

}
