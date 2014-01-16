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

package org.pmw.tinylog.policies;

import java.io.File;

import org.pmw.tinylog.LoggingLevel;

/**
 * Policies define rollover strategies for {@link org.pmw.tinylog.writers.RollingFileWriter RollingFileWriter}.
 * 
 * <p>
 * The annotation {@link org.pmw.tinylog.policies.PropertiesSupport PropertiesSupport} must be added to the implemented policy class and the implemented policy
 * must be registered as service in "META-INF/services/org.pmw.tinylog.policies" in order to make the policy available
 * by properties files and system properties.
 * </p>
 * 
 * <p>
 * Example:<br />
 * <code>
 * {@literal @}PropertiesSupport(name = "startup")<br />
 * public final class StartupPolicy implements Policy {
 * </code>
 * </p>
 * 
 * <p>
 * A policy must have a default constructor without any parameters. Optionally it can have an additional constructor
 * with a string parameter if the policy supports parameters.
 * </p>
 */
public interface Policy {

	/**
	 * Determine if a rollover should occur at startup.
	 * 
	 * @param logFile
	 *            Log file to continue
	 * @return <code>true</code> to continue the log file, <code>false</code> to trigger a rollover
	 */
	boolean initCheck(File logFile);

	/**
	 * Determine if a rollover should occur.
	 * 
	 * @param level
	 *            Logging level of log entry to write
	 * @param logEntry
	 *            Log entry to write
	 * @return <code>true</code> to continue the current log file, <code>false</code> to trigger a rollover
	 */
	boolean check(LoggingLevel level, String logEntry);

	/**
	 * The log file was rolled and reset the policy.
	 */
	void reset();

}
