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
 * Immutable storage for throwable data.
 */
public final class ThrowableStore implements ThrowableData {

	private String className;
	private String message;
	private List<StackTraceElement> stackTrace;
	private ThrowableData cause;

	/**
	 * @param className
	 *            Class name of the throwable
	 * @param message
	 *            Message of the throwable
	 * @param stackTrace
	 *            Stack trace for the throwable
	 * @param cause
	 *            Cause of the throwable (can be {@code null})
	 */
	public ThrowableStore(final String className, final String message, final List<StackTraceElement> stackTrace,
		final ThrowableData cause) {
		this.className = className;
		this.message = message;
		this.stackTrace = stackTrace;
		this.cause = cause;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public List<StackTraceElement> getStackTrace() {
		return stackTrace;
	}

	@Override
	public ThrowableData getCause() {
		return cause;
	}

}
