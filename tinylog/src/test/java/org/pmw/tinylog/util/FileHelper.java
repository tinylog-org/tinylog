/*
 * Copyright 2013 Martin Winandy
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

package org.pmw.tinylog.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.pmw.tinylog.EnvironmentHelper;

/**
 * Helper to create temporary files and write into files.
 */
public final class FileHelper {

	private FileHelper() {
	}

	/**
	 * Create an empty temporary file in the workspace. It will be deleted automatically on exit.
	 * 
	 * @param extension
	 *            File extension without '.'
	 * @return The created temporary file
	 * @throws IOException
	 *             Failed to create a temporary file
	 */
	public static File createTemporaryFileInWorkspace(final String extension) throws IOException {
		File file = File.createTempFile("tmp", extension == null ? "" : "." + extension, new File("").getAbsoluteFile());
		file.deleteOnExit();
		return file;
	}

	/**
	 * Create an empty temporary file. It will be deleted automatically on exit.
	 * 
	 * @param extension
	 *            File extension without '.'
	 * @return The created temporary file
	 * @throws IOException
	 *             Failed to create a temporary file
	 */
	public static File createTemporaryFile(final String extension) throws IOException {
		File file = File.createTempFile("tmp", extension == null ? "" : "." + extension);
		file.deleteOnExit();
		return file;
	}

	/**
	 * Create a textual temporary file. It will be deleted automatically on exit.
	 * 
	 * @param extension
	 *            File extension without '.'
	 * @param lines
	 *            Lines of the file (will be separated by the line separator of the current platform)
	 * @return The created temporary file
	 * @throws IOException
	 *             Failed to create a temporary file
	 */
	public static File createTemporaryFile(final String extension, final String... lines) throws IOException {
		return createTemporaryFile(extension, toString(lines));
	}

	/**
	 * Create a textual temporary file. It will be deleted automatically on exit.
	 * 
	 * @param extension
	 *            File extension without '.'
	 * @param content
	 *            Content of the file
	 * @return The created temporary file
	 * @throws IOException
	 *             Failed to create a temporary file
	 */
	public static File createTemporaryFile(final String extension, final String content) throws IOException {
		File file = createTemporaryFile(extension);
		write(file, content);
		return file;
	}

	/**
	 * Read the content of a file.
	 * 
	 * @param file
	 *            File to get content from
	 * @return Content of file as string
	 * @throws IOException
	 *             Failed to read file
	 */
	public static String read(final File file) throws IOException {
		StringBuilder builder = new StringBuilder((int) file.length());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			char[] buffer = new char[1024];
			int length = 0;
			while ((length = reader.read(buffer)) != -1) {
				builder.append(buffer, 0, length);
			}
			return builder.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Write lines into a textual file (current content will be replaced).
	 * 
	 * @param file
	 *            File to write to
	 * @param lines
	 *            Lines to write (will be separated by the line separator of the current platform)
	 * @throws IOException
	 *             Failed to write to file
	 */
	public static void write(final File file, final String... lines) throws IOException {
		write(file, toString(lines));
	}

	/**
	 * Write a text into a textual file (current content will be replaced).
	 * 
	 * @param file
	 *            File to write to
	 * @param content
	 *            Content to write
	 * @throws IOException
	 *             Failed to write to file
	 */
	public static void write(final File file, final String content) throws IOException {
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			writer.write(content);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static String toString(final String... lines) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < lines.length; ++i) {
			if (i > 0) {
				builder.append(EnvironmentHelper.getNewLine());
			}
			builder.append(lines[i].replace("\\", "\\\\"));
		}
		return builder.toString();
	}

}
