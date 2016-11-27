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

import java.lang.management.ManagementFactory;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Runtime dialect implementation for Sun's and Oracle's Java Virtual Machines.
 */
final class JavaRuntime implements RuntimeDialect {

	/** */
	JavaRuntime() {
	}

	@Override
	public int getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		try {
			return Integer.parseInt(name.substring(0, name.indexOf('@')));
		} catch (NumberFormatException ex) {
			InternalLogger.log(Level.ERROR, "Illegal process ID: " + name.substring(0, name.indexOf('@')));
			return -1;
		} catch (IndexOutOfBoundsException ex) {
			InternalLogger.log(Level.ERROR, "Name of virtual machine does not contain a process ID: " + name);
			return -1;
		}
	}

}
