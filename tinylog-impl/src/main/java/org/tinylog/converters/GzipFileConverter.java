/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.converters;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Converter for compressing a log file asynchronously by the GZIP algorithm when backing up the file.
 */
public final class GzipFileConverter implements FileConverter {

	private static final String THREAD_NAME_PREFIX = "tinylog-GZipThread-";

	private static final AtomicInteger count = new AtomicInteger();

	private volatile File file;

	/** */
	public GzipFileConverter() {
	}

	@Override
	public String getBackupSuffix() {
		return GzipEncoder.FILE_EXTENSION;
	}

	@Override
	public void open(final String fileName) {
		file = new File(fileName);
	}

	@Override
	public byte[] write(final byte[] data) {
		return data;
	}

	@Override
	public void close() {
		Thread thread = new Thread(new GzipEncoder(file));
		thread.setName(THREAD_NAME_PREFIX + count.getAndIncrement());
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

}
