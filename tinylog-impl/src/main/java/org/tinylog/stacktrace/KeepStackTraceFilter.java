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

import java.util.List;

/**
 * Filter for removing all stack trace elements from stack trace except the defined packages and classes.
 */
public final class KeepStackTraceFilter extends AbstractStackTraceFilter {

	/**
	 * @param arguments
	 *            List of packages and classes, separated by '|'
	 */
	public KeepStackTraceFilter(final String arguments) {
		super(arguments);
	}
	
	@Override
	protected boolean shouldKept(final String className, final List<String> filters) {
		for (String filter : filters) {
			if (match(className, filter)) {
				return true;
			}
		}
		
		return false;
	}

}
