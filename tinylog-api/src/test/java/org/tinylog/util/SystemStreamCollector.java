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

package org.tinylog.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Collector for standard output stream {@link System#out} and error output stream {@link System#err}.
 *
 * <p>
 * All outputs to one of the streams will be stored instead of being displayed in the console. Collecting can be stopped
 * by {@link #close()}.
 * </p>
 */
public final class SystemStreamCollector implements AutoCloseable {

	private ByteArrayOutputStream standardStream;
	private ByteArrayOutputStream errorStream;

	private PrintStream originalStandardStream;
	private PrintStream originalErrorStream;

	/** */
	public SystemStreamCollector() {
		originalStandardStream = System.out;
		originalErrorStream = System.err;

		standardStream = new ByteArrayOutputStream();
		errorStream = new ByteArrayOutputStream();

		try {
			System.setOut(new PrintStream(standardStream, true, StandardCharsets.UTF_8.name()));
			System.setErr(new PrintStream(errorStream, true, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException ex) {
			// UTF-8 should be supported on all platforms
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Consumes all available outputs from standard output stream.
	 *
	 * <p>
	 * The standard output stream will be emptied afterwards.
	 * </p>
	 *
	 * @return All available outputs as a single string
	 */
	public String consumeStandardOutput() {
		byte[] data;
		synchronized (standardStream) {
			data = standardStream.toByteArray();
			standardStream.reset();
		}
		return new String(data, StandardCharsets.UTF_8);
	}

	/**
	 * Consumes all available outputs from error output stream.
	 *
	 * <p>
	 * The error output stream will be emptied afterwards.
	 * </p>
	 *
	 * @return All available outputs as a single string
	 */
	public String consumeErrorOutput() {
		byte[] data;
		synchronized (errorStream) {
			data = errorStream.toByteArray();
			errorStream.reset();
		}
		return new String(data, StandardCharsets.UTF_8);
	}

	/**
	 * Drops stored output from standard and error output stream.
	 */
	public void clear() {
		synchronized (standardStream) {
			standardStream.reset();
		}
		synchronized (errorStream) {
			errorStream.reset();
		}
	}

	/**
	 * Stops collecting outputs form standard output stream {@link System#out} and error output stream
	 * {@link System#err}.
	 */
	@Override
	public void close() {
		System.setOut(originalStandardStream);
		System.setErr(originalErrorStream);
	}

}
