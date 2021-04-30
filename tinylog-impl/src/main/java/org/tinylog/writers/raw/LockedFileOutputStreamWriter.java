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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Wrapper for using {@link FileOutputStream} as writer. In opposite to {@link OutputStreamWriter}, this writer supports
 * only {@link FileOutputStream FileOutputStreams} and uses {@link FileLock FileLocks} to support writing from multiple
 * processes to the same file.
 */
public final class LockedFileOutputStreamWriter implements ByteArrayWriter {

	private final FileOutputStream stream;

	/**
	 * @param stream
	 *            Underlying output stream
	 */
	public LockedFileOutputStreamWriter(final FileOutputStream stream) {
		this.stream = stream;
	}

	@Override
	public void write(final byte[] data, final int length) throws IOException {
		write(data, 0, length);
	}

	@Override
	public void write(final byte[] data, final int offset, final int length) throws IOException {
		FileChannel channel = stream.getChannel();
		FileLock lock = channel.lock();
		try {
			channel.position(channel.size());
			stream.write(data, offset, length);
		} finally {
			lock.release();
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}

}
