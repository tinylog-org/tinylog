/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.path;

import org.tinylog.runtime.RuntimeProvider;
import org.tinylog.runtime.Timestamp;

/**
 * Path segment that represents the process ID.
 */
final class ProcessIdSegment implements Segment {

	private final String pid;

	/** */
	ProcessIdSegment() {
		pid = Long.toString(RuntimeProvider.getProcessId());
	}

	@Override
	public String getStaticText() {
		return pid;
	}

	@Override
	public boolean validateToken(final String token) {
		return pid.equals(token);
	}

	@Override
	public String createToken(final String prefix, final Timestamp timestamp) {
		return pid;
	}

}
