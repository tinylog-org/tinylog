/*
 * Copyright 2014 Martin Winandy
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

package org.pmw.benchmark.dummy;

import java.io.File;

import org.pmw.benchmark.ILoggingFramework;

public class Dummy implements ILoggingFramework {

	@Override
	public String getName() {
		return "dummy";
	}

	@Override
	public void init(final File file) throws Exception {
		// Do nothing
	}

	@Override
	public void trace(final Object obj) {
		// Ignore
	}

	@Override
	public void debug(final Object obj) {
		// Ignore
	}

	@Override
	public void info(final Object obj) {
		// Ignore
	}

	@Override
	public void warning(final Object obj) {
		// Ignore
	}

	@Override
	public void error(final Object obj) {
		// Ignore
	}

	@Override
	public void dispose() throws Exception {
		// Do nothing
	}

}
