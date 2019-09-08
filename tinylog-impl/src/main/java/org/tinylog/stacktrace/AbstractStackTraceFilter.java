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

public abstract class AbstractStackTraceFilter implements StackTraceFilter {

	private static final StackTraceElement[] EMPTY_TRACE = new StackTraceElement[0];

	private final List<String> packagesAndClasses;

	/**
	 * @param argument
	 *            List of packages and classes, separated by '|'
	 */
	public AbstractStackTraceFilter(final String argument) {
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
			if (shouldKept(element.getClassName(), packagesAndClasses)) {
				newTrace.add(element);
			}
		}

		throwable.setStackTrace(newTrace.toArray(EMPTY_TRACE));

		if (throwable.getCause() != null) {
			apply(throwable.getCause());
		}

		return throwable;
	}

	/**
	 * Tests if the stack trace element of a passed class name should be kept.
	 * 
	 * @param className
	 *            Fully-qualified class name to match
	 * @param filters
	 *            Filters to apply
	 * @return {@code true} if the stack trace element of the passed class name should be kept, {@code false} if not
	 */
	protected abstract boolean shouldKept(String className, List<String> filters);

	/**
	 * Tests if a passed filter matches a given fully-qualified class name.
	 * 
	 * @param className
	 *            Fully-qualified class name to match
	 * @param filter
	 *            Filter to apply
	 * @return {@code true} if the filter matches the given class name, {@code false} if not
	 */
	protected boolean match(final String className, final String filter) {
		return className.startsWith(filter) && (filter.length() == className.length() || className.charAt(filter.length()) == '.');
	}

}
