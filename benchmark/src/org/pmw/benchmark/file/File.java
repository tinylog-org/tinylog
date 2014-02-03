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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.pmw.benchmark.ILoggingFramework;

public class File implements ILoggingFramework {

	private static final String TIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	private static final String THREAD = Thread.currentThread().getName();
	private static final String CLASS = File.class.getName();

	private static final String INFO_STRING = TIME + " [" + THREAD + "] " + CLASS + ".info(): Info: ???";
	private static final String WARNING_STRING = TIME + " [" + THREAD + "] " + CLASS + ".warn(): WARNING: ???";
	private static final String ERROR_STRING = TIME + " [" + THREAD + "] " + CLASS + ".error(): ERROR: ???";

	private static final byte[] INFO_LINE = (INFO_STRING + System.getProperty("line.separator")).getBytes();
	private static final byte[] WARNING_LINE = (WARNING_STRING + System.getProperty("line.separator")).getBytes();
	private static final byte[] ERROR_LINE = (ERROR_STRING + System.getProperty("line.separator")).getBytes();

	private OutputStream stream;

	@Override
	public String getName() {
		return "file";
	}

	@Override
	public void init(final java.io.File file) throws Exception {
		stream = new FileOutputStream(file.getAbsolutePath());
	}

	@Override
	public void trace(final Object obj) {
		// Ignore
	}

	@Override
	public void debug(final Object obj) {
		// Ignore

	}

	@Override
	public void info(final Object obj) {
		try {
			stream.write(INFO_LINE);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void warning(final Object obj) {
		try {
			stream.write(WARNING_LINE);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void error(final Object obj) {
		try {
			stream.write(ERROR_LINE);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void dispose() throws Exception {
		stream.close();
	}

}
