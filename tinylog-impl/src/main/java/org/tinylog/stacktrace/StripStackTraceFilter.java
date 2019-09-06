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
 * Filter for removing defined packages and classes from stack trace.
 */
public final class StripStackTraceFilter implements StackTraceFilter {

	private static final StackTraceElement[] EMPTY_TRACE = new StackTraceElement[0];

	private final List<String> packagesAndClasses;

	/**
	 * @param argument
	 *            List of packages and classes, separated by '|'
	 */
	public StripStackTraceFilter(final String argument) {
		this.packagesAndClasses = new ArrayList<String>();

		if (argument != null) {
			for (String token : argument.split("\\|")) {
				token = token.trim();
				if (!token.isEmpty()) {
					packagesAndClasses.add(token);
				}
			}
		}
	}

	@Override
	public Throwable apply(final Throwable throwable) {
		StackTraceElement[] currentTrace = throwable.getStackTrace();
		List<StackTraceElement> newTrace = new ArrayList<StackTraceElement>(currentTrace.length);

		for (StackTraceElement element : currentTrace) {
			String className = element.getClassName();
			boolean skip = false;
			
			for (String filter : packagesAndClasses) {
				if (className.startsWith(filter) && (filter.length() == className.length() || className.charAt(filter.length()) == '.')) {
					skip = true;
					break;
				}
			}
			
			if (!skip) {
				newTrace.add(element);
			}
		}

		throwable.setStackTrace(newTrace.toArray(EMPTY_TRACE));

		if (throwable.getCause() != null) {
			apply(throwable.getCause());
		}

		return throwable;
	}

}
