/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Doesn't output anything, only the used flag will be set if anything was written.
 */
public class SilentOutputStream extends OutputStream {

	private boolean isUsed;

	/** */
	public SilentOutputStream() {
		isUsed = false;
	}

	/**
	 * Returns the used flag.
	 * 
	 * @return <code>true</code> if anything was written, otherwise <code>false</code>
	 */
	public final boolean isUsed() {
		return isUsed;
	}

	@Override
	public final void write(final int b) throws IOException {
		isUsed = true;
	}

}
