/*
 * Copyright 2017 Martin Winandy
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

package org.tinylog.benchmarks.api;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Benchmark for comparing writing performance of different classes that Java provides for writing to a file.
 *
 * @see FileOutputStream
 * @see RandomAccessFile
 * @see FileChannel
 */
public class WritingBenchmark {

	private static final String LINE = "Writing lines to a plain text file with Java I/O API - Let's benchmark it!\n";
	private static final byte[] DATA = LINE.getBytes(StandardCharsets.US_ASCII);

	private static final long LINES = 1_000_000;
	private static final int BUFFER_CAPACITY = 64 * 1024;

	/** */
	public WritingBenchmark() {
	}

	/**
	 * Benchmarks writing via {@link FileOutputStream}, wrapped by {@link BufferedOutputStream} for buffering.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void bufferedFileOutputStream(final Configuration configuration) throws IOException {
		try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(configuration.file))) {
			for (long i = 0; i < LINES; ++i) {
				stream.write(DATA);
			}
		}
	}

	/**
	 * Benchmarks writing via {@link FileOutputStream} with custom buffering via a byte array.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void byteArrayFileOutputStream(final Configuration configuration) throws IOException {
		byte[] buffer = new byte[BUFFER_CAPACITY];
		int position = 0;

		try (OutputStream stream = new FileOutputStream(configuration.file)) {
			for (long i = 0; i < LINES; ++i) {
				if (BUFFER_CAPACITY - position < DATA.length) {
					stream.write(buffer, 0, position);
					position = 0;
				}

				if (BUFFER_CAPACITY < DATA.length) {
					stream.write(DATA);
				} else {
					System.arraycopy(DATA, 0, buffer, position, DATA.length);
					position += DATA.length;
				}
			}

			if (position > 0) {
				stream.write(buffer, 0, position);
			}
		}
	}

	/**
	 * Benchmarks writing via {@link RandomAccessFile} with custom buffering via a byte array.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void byteArrayRandomAccessFile(final Configuration configuration) throws IOException {
		byte[] buffer = new byte[BUFFER_CAPACITY];
		int position = 0;

		try (RandomAccessFile file = new RandomAccessFile(configuration.file, "rw")) {
			for (long i = 0; i < LINES; ++i) {
				if (BUFFER_CAPACITY - position < DATA.length) {
					file.write(buffer, 0, position);
					position = 0;
				}

				if (BUFFER_CAPACITY < DATA.length) {
					file.write(DATA);
				} else {
					System.arraycopy(DATA, 0, buffer, position, DATA.length);
					position += DATA.length;
				}

			}

			if (position > 0) {
				file.write(buffer, 0, position);
			}
		}
	}

	/**
	 * Benchmarks writing via {@link FileOutputStream} with using a {@link ByteBuffer} for buffering.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void byteBufferFileOutputStream(final Configuration configuration) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

		try (OutputStream stream = new FileOutputStream(configuration.file)) {
			for (long i = 0; i < LINES; ++i) {
				if (buffer.remaining() < DATA.length) {
					stream.write(buffer.array(), 0, buffer.position());
					buffer.rewind();
				}

				if (buffer.remaining() < DATA.length) {
					stream.write(DATA);
				} else {
					buffer.put(DATA);
				}
			}

			if (buffer.position() > 0) {
				stream.write(buffer.array(), 0, buffer.position());
			}
		}
	}

	/**
	 * Benchmarks writing via {@link RandomAccessFile} with using a {@link ByteBuffer} for buffering.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void byteBufferRandomAccessFile(final Configuration configuration) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

		try (RandomAccessFile file = new RandomAccessFile(configuration.file, "rw")) {
			for (long i = 0; i < LINES; ++i) {
				if (buffer.remaining() < DATA.length) {
					file.write(buffer.array(), 0, buffer.position());
					buffer.rewind();
				}

				if (buffer.remaining() < DATA.length) {
					file.write(DATA);
				} else {
					buffer.put(DATA);
				}
			}

			if (buffer.position() > 0) {
				file.write(buffer.array(), 0, buffer.position());
			}
		}
	}

	/**
	 * Benchmarks writing via {@link FileChannel} with using a {@link ByteBuffer} for buffering.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void byteBufferFileChannel(final Configuration configuration) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);

		try (RandomAccessFile file = new RandomAccessFile(configuration.file, "rw")) {
			try (FileChannel channel = file.getChannel()) {
				for (long i = 0; i < LINES; ++i) {
					if (buffer.remaining() < DATA.length) {
						buffer.flip();
						channel.write(buffer);
						buffer.clear();
					}

					if (buffer.remaining() < DATA.length) {
						file.write(DATA);
					} else {
						buffer.put(DATA);
					}
				}

				if (buffer.position() > 0) {
					buffer.flip();
					channel.write(buffer);
				}
			}
		}
	}

	/**
	 * Benchmarks direct writing via {@link FileOutputStream} without using any kind of buffering.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void unbufferedFileOutputStream(final Configuration configuration) throws IOException {
		try (OutputStream stream = new FileOutputStream(configuration.file)) {
			for (long i = 0; i < LINES; ++i) {
				stream.write(DATA);
			}
		}
	}

	/**
	 * Benchmarks direct writing via {@link RandomAccessFile} without using any kind of buffering.
	 *
	 * @param configuration
	 *            Configuration with target file
	 * @throws IOException
	 *             Failed to write to target file
	 */
	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	public void unbufferedRandomAccessFile(final Configuration configuration) throws IOException {
		try (RandomAccessFile file = new RandomAccessFile(configuration.file, "rw")) {
			for (long i = 0; i < LINES; ++i) {
				file.write(DATA);
			}
		}
	}

	/**
	 * Holder of target file.
	 */
	@State(Scope.Thread)
	public static class Configuration {

		private File file;

		/** */
		public Configuration() {
		}

		/**
		 * Creates a new empty temporary file.
		 *
		 * @throws IOException
		 *             Failed to create new temporary file.
		 */
		@Setup(Level.Invocation)
		public void init() throws IOException {
			file = File.createTempFile("log", ".txt");
			file.deleteOnExit();
		}

		/**
		 * Verifies that all lines were written and deletes the temporary file afterwards.
		 */
		@TearDown(Level.Invocation)
		public void dispose() {
			try {
				if (file.length() != LINES * DATA.length) {
					throw new IllegalStateException("File is corrupt (" + (LINES * DATA.length)
						+ " bytes expected, but was " + file.length() + " bytes)");
				}
			} finally {
				file.delete();
			}
		}

	}

}
