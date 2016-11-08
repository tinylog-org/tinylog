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

package org.tinylog.rules;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Collector for standard output stream {@link System#out} and error output stream {@link System#err}.
 *
 * <p>
 * All outputs to one of the streams will be stored instead of being displayed in the console. Collecting can be stopped
 * by {@link #stop()}.
 * </p>
 */
public final class SystemStreamCollector implements TestRule {

	private final boolean verifiyEmptyStreams;

	private final ByteArrayOutputStream standardStream;
	private final ByteArrayOutputStream errorStream;

	private final PrintStream originalStandardStream;
	private final PrintStream originalErrorStream;

	/**
	 * @param verifiyEmptyStreams
	 *            if {@code true}, output streams while be checked whether they are empty after each test method,
	 *            otherwise the streams will be cleaned silently
	 */
	public SystemStreamCollector(final boolean verifiyEmptyStreams) {
		this.verifiyEmptyStreams = verifiyEmptyStreams;

		standardStream = new ByteArrayOutputStream();
		errorStream = new ByteArrayOutputStream();

		originalStandardStream = System.out;
		originalErrorStream = System.err;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				start();
				try {
					base.evaluate();
				} finally {
					stop();
				}
			}
		};
	}

	/**
	 * Starts collecting outputs form standard output stream {@link System#out} and error output stream
	 * {@link System#err} instead of writing them to console.
	 */
	public void start() {
		try {
			System.setOut(new PrintStream(standardStream, true, StandardCharsets.UTF_8.name()));
			System.setErr(new PrintStream(errorStream, true, StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException ex) {
			// UTF-8 should be supported on all platforms
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Stops collecting outputs form standard output stream {@link System#out} and error output stream
	 * {@link System#err}.
	 */
	public void stop() {
		System.setOut(originalStandardStream);
		System.setErr(originalErrorStream);

		if (verifiyEmptyStreams) {
			Assertions.assertThat(consumeStandardOutput()).isEmpty();
			Assertions.assertThat(consumeErrorOutput()).isEmpty();
		}

		clear();
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

}
