/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.core.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * JUnit extension annotation for capturing output log entries.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtendWith(LogCaptureExtension.class)
public @interface CaptureLogEntries {

	/**
	 * Key value pairs to apply to the framework's configuration.
	 *
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * {@literal @}CaptureLogEntries(configuration = "locale=en_US")
	 * {@literal @}CaptureLogEntries(configuration = {"backend=nop", "locale=en_US"})
	 * </pre></blockquote>
	 * </p>
	 *
	 * @return The initial configuration to apply to the framework
	 */
	String[] configuration() default "";

}
