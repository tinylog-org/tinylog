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

package org.pmw.tinylog.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Deque;
import java.util.LinkedList;

import org.pmw.tinylog.EnvironmentHelper;

/**
 * Saves all written lines as a list of strings.
 */
public final class StringListOutputStream extends OutputStream {

	private final Deque<CharSequence> lines;

	/** */
	public StringListOutputStream() {
		lines = new LinkedList<CharSequence>();
	}

	/**
	 * Test if there are any stored lines.
	 * 
	 * @return <code>true</code> if there are any lines, <code>false</code> if not
	 */
	public boolean hasLines() {
		synchronized (lines) {
			return lines.size() > 0;
		}
	}

	/**
	 * Retrieve and remove the first line.
	 * 
	 * @return The first line or <code>null</code> if there is no line
	 */
	public String nextLine() {
		synchronized (lines) {
			CharSequence line = lines.pollFirst();
			if (line == null) {
				return null;
			} else {
				return line.toString();
			}
		}
	}

	/**
	 * Remove all lines.
	 */
	public void clear() {
		synchronized (lines) {
			lines.clear();
		}
	}

	@Override
	public void write(final int b) throws IOException {
		synchronized (lines) {
			CharSequence lastLine = lines.peekLast();
			if (lastLine == null || lastLine instanceof String) {
				lastLine = new StringBuilder();
				lines.add(lastLine);
			}

			if (b == '\n') {
				lines.removeLast();
				lines.add(lastLine.toString());
			} else if (b != '\r') {
				((StringBuilder) lastLine).append((char) b);
			}
		}
	}

	@Override
	public String toString() {
		synchronized (lines) {
			String[] array = lines.toArray(new String[0]);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				if (i > 0) {
					builder.append(EnvironmentHelper.getNewLine());
				}
				builder.append(array[i]);
			}
			return builder.toString();
		}
	}

}
