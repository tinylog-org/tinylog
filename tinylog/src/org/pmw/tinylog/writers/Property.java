/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.writers;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation specifies a property for a {@link org.pmw.tinylog.writers.Writer Writer}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

	/**
	 * Name of the property without the prefix "tinylog.writer.".
	 */
	String name();

	/**
	 * Type of the property. Currently are supported:
	 *
	 * <ul>
	 * <li><code>boolean.class</code></li>
	 * <li><code>int.class</code></li>
	 * <li><code>String.class</code></li>
	 * <li><code>String[].class</code></li>
	 * <li><code>Labeler.class</code></li>
	 * <li><code>Policy.class</code></li>
	 * <li><code>Policy[].class</code></li>
	 * </ul>
	 */
	Class<?> type();

	/**
	 * Define if the property is optional. Default is <code>false</code>.
	 */
	boolean optional() default false;

}
