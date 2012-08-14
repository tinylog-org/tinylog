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

package org.pmw.benchmark.tinylog;

import java.io.File;

import org.pmw.benchmark.IBenchmark;
import org.pmw.tinylog.ELoggingLevel;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.FileWriter;

public class TinylogBenchmark implements IBenchmark {

	private FileWriter writer;

	@Override
	public String getName() {
		return "tinylog";
	}

	@Override
	public void log(final int index) {
		Logger.trace("Trace: {0}, PI: {1}", index, Math.PI);
		Logger.debug("Debug: {0}, PI: {1}", index, Math.PI);
		Logger.info("Info: {0}, PI: {1}", index, Math.PI);
		Logger.warn("Warning: {0}, PI: {1}", index, Math.PI);
		Logger.error("Error: {0}, PI: {1}", index, Math.PI);
	}

	@Override
	public void init(final File file) throws Exception {
		writer = new FileWriter(file.getAbsolutePath());
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.INFO);
		Logger.setLoggingFormat("{date:yyyy-MM-dd HH:mm:ss} [{thread}] {class}.{method}(): {message}");
	}

	@Override
	public void dispose() throws Exception {
		writer.close();
	}

}
