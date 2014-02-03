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

package org.pmw.benchmark.log4j;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.FileAppender;

public class Log4jWithAsyncAppender extends Log4j {

	@Override
	public String getName() {
		return "log4j with async appender";
	}

	@Override
	protected Appender createAppender(final File file) throws IOException {
		AsyncAppender appender = new AsyncAppender();
		appender.addAppender(new FileAppender(createLayout(), file.getAbsolutePath(), false, true, 64 * 1024));
		return appender;
	}

}
