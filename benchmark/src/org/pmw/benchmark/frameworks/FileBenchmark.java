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

package org.pmw.benchmark.frameworks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.pmw.benchmark.Benchmark;

/**
 * Write log entries to a file without using any logging framework.
 */
public final class FileBenchmark implements Benchmark {

	private static final String NAME = "file";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final String THREAD = Thread.currentThread().getName();
	private static final String LINES_SEPARATOR = System.getProperty("line.separator");

	private OutputStream stream;

	public FileBenchmark() {
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void init(final File file) throws FileNotFoundException {
		stream = new FileOutputStream(file.getAbsolutePath());
	}

	@Override
	public void write(final long number) throws IOException {
		stream.write(new StringBuilder(DATE_FORMAT.format(new Date())).append(" [").append(THREAD).append("] ").append(FileBenchmark.class.getName())
				.append(".write(): Info: ").append(number).append(LINES_SEPARATOR).toString().getBytes());
		stream.write(new StringBuilder().append(DATE_FORMAT.format(new Date())).append(" [").append(THREAD).append("] ").append(FileBenchmark.class.getName())
				.append(".write(): Warning: ").append(number).append(LINES_SEPARATOR).toString().getBytes());
		stream.write(new StringBuilder().append(DATE_FORMAT.format(new Date())).append(" [").append(THREAD).append("] ").append(FileBenchmark.class.getName())
				.append(".write(): Error: ").append(number).append(LINES_SEPARATOR).toString().getBytes());
	}

	@Override
	public boolean calculate(final List<Long> primes, final long number) throws IOException {
		for (Long prime : primes) {
			if (number % prime == 0L) {
				return false;
			}
		}
		stream.write(new StringBuilder(DATE_FORMAT.format(new Date())).append(" [").append(THREAD).append("] ").append(FileBenchmark.class.getName())
				.append(".calculate(): ").append(number).append(" is prime").append(LINES_SEPARATOR).toString().getBytes());
		return true;
	}

	@Override
	public void dispose() throws IOException {
		stream.close();
	}

}
