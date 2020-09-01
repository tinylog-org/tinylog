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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * GZIP encoder for compressing a file.
 */
final class GzipEncoder implements Runnable {

	/**
	 * File extension for compressed GZIP files.
	 */
	static final String FILE_EXTENSION = ".gz";

	private static final int BUFFER_SIZE = 64 * 1024;

	private final File sourceFile;
	private final File targetFile;

	/**
	 * @param file
	 *            File that should be compressed
	 */
	GzipEncoder(final File file) {
		sourceFile = file;
		targetFile = new File(file.getAbsolutePath() + FILE_EXTENSION);
	}

	@Override
	public void run() {
		try {
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
				try {
					GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream, BUFFER_SIZE);
					try {
						byte[] buffer = new byte[BUFFER_SIZE];
						int count;
						while ((count = fileInputStream.read(buffer)) >= 0) {
							gzipOutputStream.write(buffer, 0, count);
						}
					} finally {
						gzipOutputStream.close();
					}
				} finally {
					fileOutputStream.close();
				}
			} finally {
				fileInputStream.close();
			}

			if (!sourceFile.delete()) {
				InternalLogger.log(Level.WARN, "Failed to delete original log file '" + sourceFile + "'");
			}
		} catch (IOException ex) {
			InternalLogger.log(Level.ERROR, ex, "Failed to compress log file '" + sourceFile + "'");
		}
	}

}
