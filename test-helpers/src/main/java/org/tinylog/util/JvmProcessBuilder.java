/*
 * Copyright 2017 Martin Winandy
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder for creating new JVM processes.
 */
public final class JvmProcessBuilder {

	private static final String SEPARATOR = System.getProperty("file.separator");
	private static final String CLASSPATH = System.getProperty("java.class.path");
	private static final String JAVA = System.getProperty("java.home") + SEPARATOR + "bin" + SEPARATOR + "java";

	private final ProcessBuilder builder;

	/**
	 * @param application
	 *            Class with main method
	 * @param arguments
	 *            Arguments for main method
	 */
	public JvmProcessBuilder(final Class<?> application, final String... arguments) {
		List<String> command = new ArrayList<>();
		command.add(JAVA);
		command.add("-cp");
		command.add(CLASSPATH);
		command.add(application.getCanonicalName());
		command.addAll(Arrays.asList(arguments));

		builder = new ProcessBuilder(command.toArray(new String[0]));
		builder.redirectErrorStream(true);
	}

	/**
	 * Starts a new process using the attributes of this JVM process builder.
	 * 
	 * @return A new JVM process
	 * @throws IOException
	 *             Failed starting process
	 */
	public Process start() throws IOException {
		return builder.start();
	}

	/**
	 * Starts multiple new processes using the attributes of this JVM process builder.
	 * 
	 * @param count
	 *            Number of processes to start
	 * @return New JVM processes
	 * @throws IOException
	 *             Failed starting process
	 */
	public List<Process> start(final int count) throws IOException {
		List<Process> processes = new ArrayList<>(count);
		for (int i = 0; i < count; ++i) {
			processes.add(builder.start());
		}
		return processes;
	}

}
