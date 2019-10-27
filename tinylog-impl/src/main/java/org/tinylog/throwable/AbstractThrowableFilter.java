/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.throwable;

import java.util.List;

/**
 * Abstract throwable filter that just loops trough all data of the passed origin throwable data.
 */
public abstract class AbstractThrowableFilter implements ThrowableFilter {

	private final List<String> arguments;

	/**
	 * @param arguments
	 *            Configured arguments
	 */
	public AbstractThrowableFilter(final List<String> arguments) {
		this.arguments = arguments;
	}

	/**
	 * Gets all passed arguments.
	 * 
	 * @return Passed arguments
	 */
	protected final List<String> getArguments() {
		return arguments;
	}

}
