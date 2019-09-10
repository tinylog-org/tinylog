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

import java.util.Arrays;
import java.util.List;

/**
 * Adapter for using any kind of throwables as a stack trace filter.
 * 
 * <p>
 * All data of the passed throwable will be just looped trough.
 * </p>
 */
public final class StackTraceFilterAdapter implements StackTraceFilter {

	private final Throwable throwable;

	/**
	 * @param throwable
	 *            Origin source throwable
	 */
	public StackTraceFilterAdapter(final Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public String getClassName() {
		return throwable.getClass().getName();
	}

	@Override
	public String getMessage() {
		return throwable.getMessage();
	}

	@Override
	public List<StackTraceElement> getStackTrace() {
		return Arrays.asList(throwable.getStackTrace());
	}

	@Override
	public StackTraceFilterAdapter getCause() {
		return throwable.getCause() == null ? null : new StackTraceFilterAdapter(throwable.getCause());
	}

}
