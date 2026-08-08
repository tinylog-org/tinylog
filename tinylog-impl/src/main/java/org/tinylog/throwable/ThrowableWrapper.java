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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Wrapper for exceptions and other throwables.
 */
public final class ThrowableWrapper implements ThrowableData {

	private static final Throwable[] EMPTY_THROWABLES = new Throwable[0];
	private static final Method GET_SUPPRESSED_METHOD = resolveGetSuppressedMethod();

	private final Throwable throwable;

	/**
	 * @param throwable
	 *            Throwable to wrap
	 */
	public ThrowableWrapper(final Throwable throwable) {
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
	public ThrowableWrapper getCause() {
		return throwable.getCause() == null ? null : new ThrowableWrapper(throwable.getCause());
	}

	@Override
	public List<ThrowableData> getSuppressed() {
		Throwable[] suppressed = extractSuppressed(throwable);
		if (suppressed.length == 0) {
			return Collections.emptyList();
		}

		List<ThrowableData> result = new ArrayList<ThrowableData>(suppressed.length);
		for (int i = 0; i < suppressed.length; ++i) {
			result.add(new ThrowableWrapper(suppressed[i]));
		}
		return result;
	}

	/**
	 * Resolves {@link Throwable#getSuppressed()} reflectively for Java 6 source compatibility.
	 *
	 * @return Method handle or {@code null} if unavailable
	 */
	private static Method resolveGetSuppressedMethod() {
		try {
			return Throwable.class.getMethod("getSuppressed");
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	/**
	 * Extracts suppressed throwables if the runtime supports them.
	 *
	 * @param throwable
	 *            Source throwable
	 * @return Suppressed throwables or an empty array
	 */
	private static Throwable[] extractSuppressed(final Throwable throwable) {
		if (GET_SUPPRESSED_METHOD == null) {
			return EMPTY_THROWABLES;
		}

		try {
			return (Throwable[]) GET_SUPPRESSED_METHOD.invoke(throwable);
		} catch (IllegalAccessException ex) {
			return EMPTY_THROWABLES;
		} catch (InvocationTargetException ex) {
			return EMPTY_THROWABLES;
		}
	}

}
