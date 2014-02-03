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

package org.pmw.benchmark.log4j2;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.async.AsyncLogger;

public class Log4j2WithAsyncLogger extends Log4j2 {

	@Override
	public String getName() {
		return "log4j 2 with async logger";
	}

	@Override
	public void dispose() throws Exception {
		AsyncLogger.stop();
		super.dispose();
	}

	@Override
	protected Logger createLogger() {
		Logger logger = super.createLogger();
		return new AsyncLogger(logger.getContext(), logger.getName(), null);
	}

}
