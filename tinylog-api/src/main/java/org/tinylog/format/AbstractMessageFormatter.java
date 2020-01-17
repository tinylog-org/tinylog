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

package org.tinylog.format;

import org.tinylog.Supplier;

/**
 * Abstract base class for message formatter implementations.
 */
public abstract class AbstractMessageFormatter implements MessageFormatter {

	protected static final int ADDITIONAL_STRING_BUILDER_CAPACITY = 32;

	/** */
	protected AbstractMessageFormatter() {
	}

	/**
	 * Resolves a potential lazy argument.
	 * 
	 * @param argument
	 *            {@link Supplier} or any other object
	 * @return Generated value, if passed argument is a {@link Supplier}, otherwise the original passed object
	 */
	protected static Object resolve(final Object argument) {
		if (argument instanceof Supplier<?>) {
			return ((Supplier<?>) argument).get();
		} else {
			return argument;
		}
	}

}
