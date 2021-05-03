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

package org.tinylog.writers.raw;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Wrapper for using a {@link RandomAccessFile} as writer.
 *
 * <p>
 *	  In opposite to {@link RandomAccessFileWriter}, this writer uses {@link FileLock FileLocks} to support writing from
 *	  multiple processes into the same file.
 * </p>
 */
public final class LockedRandomAccessFileWriter implements ByteArrayWriter {

	private final RandomAccessFile file;

	/**
	 * @param file Underlying random access file
	 */
	public LockedRandomAccessFileWriter(final RandomAccessFile file) {
		this.file = file;
	}

	@Override
	public int readTail(final byte[] data, final int offset, final int length) throws IOException {
		FileChannel channel = file.getChannel();
		FileLock lock = channel.lock();
		try {
			long fileSize = channel.size();
			int bytesToRead = (int) Math.min(fileSize, length);
			channel.position(fileSize - bytesToRead);
			return file.read(data, offset, bytesToRead);
		} finally {
			lock.release();
		}
	}

	@Override
	public void write(final byte[] data, final int length) throws IOException {
		write(data, 0, length);
	}

	@Override
	public void write(final byte[] data, final int offset, final int length) throws IOException {
		FileChannel channel = file.getChannel();
		FileLock lock = channel.lock();
		try {
			channel.position(channel.size());
			file.write(data, offset, length);
		} finally {
			lock.release();
		}
	}

	@Override
	public void truncate(final int count) throws IOException {
		FileChannel channel = file.getChannel();
		FileLock lock = channel.lock();
		try {
			file.setLength(Math.max(0, channel.size() - count));
		} finally {
			lock.release();
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

}
