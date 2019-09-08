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

package org.tinylog.stacktrace;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract stack trace filter that can accept multiple arguments, which are separated by '|'.
 */
public abstract class AbstractMultiArgumentFilter implements StackTraceFilter {

	private final List<String> arguments;

	/**
	 * @param arguments
	 *            List of arguments, separated by '|'
	 */
	public AbstractMultiArgumentFilter(final String arguments) {
		this.arguments = new ArrayList<String>();

		if (arguments != null) {
			for (String argument : arguments.split("\\|")) {
				argument = argument.trim();
				if (!argument.isEmpty()) {
					this.arguments.add(argument);
				}
			}
		}
	}
	
	public List<String> getArguments() {
		return arguments;
	}

}
