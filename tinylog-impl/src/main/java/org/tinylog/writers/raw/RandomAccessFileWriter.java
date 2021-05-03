/*
 * Copyright 2021 Martin Winandy
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

package org.tinylog.writers.raw;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Wrapper for using a {@link RandomAccessFile} as writer.
 */
public final class RandomAccessFileWriter implements ByteArrayWriter {

	private final RandomAccessFile file;

	/**
	 * @param file Underlying random access file
	 */
	public RandomAccessFileWriter(final RandomAccessFile file) {
		this.file = file;
	}

	@Override
	public int readTail(final byte[] data, final int offset, final int length) throws IOException {
		long fileLength = file.length();
		file.seek(Math.max(0, fileLength - length));
		return file.read(data, offset, (int) Math.min(fileLength, length));
	}

	@Override
	public void write(final byte[] data, final int length) throws IOException {
		write(data, 0, length);
	}

	@Override
	public void write(final byte[] data, final int offset, final int length) throws IOException {
		file.write(data, offset, length);
	}

	@Override
	public void truncate(final int count) throws IOException {
		file.setLength(Math.max(0, file.length() - count));
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

}
