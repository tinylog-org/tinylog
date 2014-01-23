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

package org.pmw.benchmark.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.pmw.benchmark.IBenchmark;

public class FileOutputStreamBenchmark implements IBenchmark {

	private static final String INFO_STRING = "2014-01-01 11:11:00 [main] org.pmw.benchmark.file.FileOutputStreamBenchmark.log(): Info: 1, PI: 3,142";
	private static final String WARNING_STRING = "2014-01-01 11:11:00 [main] org.pmw.benchmark.file.FileOutputStreamBenchmark.log(): WARNING: 100, PI: 3,142";
	private static final String ERROR_STRING = "2014-01-01 11:11:00 [main] org.pmw.benchmark.file.FileOutputStreamBenchmark.log(): ERROR: 20.000, PI: 3,142";

	private static final byte[] INFO_LINE = (INFO_STRING + System.getProperty("line.separator")).getBytes();
	private static final byte[] WARNING_LINE = (WARNING_STRING + System.getProperty("line.separator")).getBytes();
	private static final byte[] ERROR_LINE = (ERROR_STRING + System.getProperty("line.separator")).getBytes();

	private OutputStream stream;

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public void log(final int index) {
		try {
			stream.write(INFO_LINE);
			stream.write(WARNING_LINE);
			stream.write(ERROR_LINE);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void init(final File file) throws Exception {
		stream = new FileOutputStream(file.getAbsolutePath());
	}

	@Override
	public void dispose() throws Exception {
		stream.close();
	}

}
