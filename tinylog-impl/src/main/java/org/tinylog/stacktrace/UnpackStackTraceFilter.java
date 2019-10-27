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
	 * @param arguments
	 *            Configured class names of throwables to unpack
	 */
	public UnpackStackTraceFilter(final List<String> arguments) {
		super(arguments);
	}
	
	@Override
	public ThrowableData filter(final ThrowableData origin) {
		ThrowableData cause = origin.getCause();

		if (cause != null) {
			List<String> classNames = getArguments();

			if (classNames.isEmpty()) {
				return filter(cause);
			}

			for (String className : classNames) {
				if (className.equals(origin.getClassName())) {
					return filter(cause);
				}
			}
		}

		return origin;
	}

}
