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
 * Stack trace filter that drops the causes of configured throwables.
 */
public final class DropCauseStackTraceFilter extends AbstractStackTraceFilter {

	/**
	 * @param arguments
	 *            Configured class names of throwables to cut causes off
	 */
	public DropCauseStackTraceFilter(final List<String> arguments) {
		super(arguments);
	}
	
	@Override
	public ThrowableData filter(final ThrowableData origin) {
		if (getArguments().isEmpty()) {
			return new ThrowableStore(origin.getClassName(), origin.getMessage(), origin.getStackTrace(), null);
		} else {
			String className = origin.getClassName();

			for (String filter : getArguments()) {
				if (className.equals(filter)) {
					return new ThrowableStore(origin.getClassName(), origin.getMessage(), origin.getStackTrace(), null);
				}
			}
			
			return origin;
		}
	}
	
}
