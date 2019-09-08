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
 * Stack trace filter to unpack configured throwables.
 * 
 * <p>
 * The cause throwable instead of the original passed throwable will be used for all configured throwable class names,
 * if there is a cause throwable.
 * </p>
 */
public final class UnpackStackTraceFilter extends AbstractStackTraceFilter {

	/**
	 * @param origin
	 *            Origin source stack trace filter
	 * @param arguments
	 *            Configured class names of throwables to unpack
	 */
	public UnpackStackTraceFilter(final StackTraceFilter origin, final List<String> arguments) {
		super(unpack(origin, arguments), arguments);
	}

	/**
	 * Unpacks all passed throwables.
	 * 
	 * @param origin
	 *            Origin source stack trace filter
	 * @param classNames
	 *            Configured class names of throwables to unpack
	 * @return Stack trace filter to use for output
	 */
	private static StackTraceFilter unpack(final StackTraceFilter origin, final List<String> classNames) {
		StackTraceFilter cause = origin.getCause();

		if (cause != null) {
			if (classNames.isEmpty()) {
				return unpack(cause, classNames);
			}

			for (String className : classNames) {
				if (className.equals(origin.getClassName())) {
					return unpack(cause, classNames);
				}
			}
		}

		return origin;
	}

}
