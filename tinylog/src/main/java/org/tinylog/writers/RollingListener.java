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

package org.tinylog.writers;

import java.io.File;

/**
 * Listener for the rolling file writer to trigger events after rolling of log files.
 *
 * @see RollingFileWriter
 */
public interface RollingListener {

	/**
	 * Rolling file writer is initializing.
	 *
	 * @param file
	 *            Log file
	 * @throws Exception
	 *             Exception will be only logged and doesn't influence the writer
	 */
	void startup(File file) throws Exception;

	/**
	 * A log file is finished and a new one will be started.
	 *
	 * This method be called as the others synchronously. Thereby it should return very fast. Expensive IO operations
	 * should be done asynchronously.
	 *
	 * @param backup
	 *            Finished log file
	 * @param file
	 *            New log file to start
	 * @throws Exception
	 *             Exception will be only logged and doesn't influence the writer
	 */
	void rolled(File backup, File file) throws Exception;

	/**
	 * Rolling file writer is shutting down.
	 *
	 * @param file
	 *            Log file
	 * @throws Exception
	 *             Exception will be only logged and doesn't influence the writer
	 */
	void shutdown(File file) throws Exception;

}
