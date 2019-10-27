/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.throwable;

import java.util.List;

/**
 * Interface for all relevant throwable data.
 */
public interface ThrowableData {

	/**
	 * Gets the class name of the throwable to output.
	 * 
	 * @return Class name of the throwable
	 */
	String getClassName();

	/**
	 * Gets the message of the throwable to output.
	 * 
	 * @return Message of the throwable
	 */
	String getMessage();

	/**
	 * Gets the stack trace of the throwable to output.
	 * 
	 * @return Stack trace of the throwable
	 */
	List<StackTraceElement> getStackTrace();
	
	/**
	 * Gets the cause of the throwable to output.
	 * 
	 * @return Cause of the throwable or {@code null}
	 */
	ThrowableData getCause();

}
