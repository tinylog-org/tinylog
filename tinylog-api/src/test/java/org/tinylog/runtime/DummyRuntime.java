/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.runtime;

import java.util.Locale;

/**
 * Runtime dialect implementation dummy.
 */
public final class DummyRuntime extends AbstractJavaRuntime {

	private long processId = -1;
	
	/** */
	public DummyRuntime() {
	}

	@Override
	public boolean isAndroid() {
		return false;
	}

	@Override
	public long getProcessId() {
		return processId;
	}

	@Override
	public Timestamp getStartTime() {
		return null;
	}

	@Override
	public String getCallerClassName(final int depth) {
		return null;
	}

	@Override
	public String getCallerClassName(final String loggerClassName) {
		return null;
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final int depth) {
		return null;
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final String loggerClassName) {
		return null;
	}

	@Override
	public Timestamp createTimestamp() {
		return null;
	}

	@Override
	public TimestampFormatter createTimestampFormatter(final String pattern, final Locale locale) {
		return null;
	}

}
