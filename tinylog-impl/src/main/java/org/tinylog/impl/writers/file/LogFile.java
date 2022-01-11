package org.tinylog.impl.writers.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Buffered text file writer.
 */
public class LogFile implements Closeable {

	private final RandomAccessFile file;
	private final Charset charset;
	private final byte[] bom;

	private final int bufferCapacity;
	private final ByteBuffer buffer;

	/**
	 * @param fileName The path to the log file
	 * @param bufferCapacity The capacity for the byte buffer (must be greater than 0)
	 * @param charset The charset to use for wring strings
	 * @throws IOException Failed to open the log file
	 */
	public LogFile(String fileName, int bufferCapacity, Charset charset) throws IOException {
		this.file = new RandomAccessFile(fileName, "rw");
		this.charset = charset;
		this.bom = createBom(charset);

		long fileLength = this.file.length();
		long maxBufferSize = bufferCapacity - (fileLength % bufferCapacity);

		this.buffer = new ByteBuffer(bufferCapacity, (int) maxBufferSize);
		this.bufferCapacity = bufferCapacity;

		if (fileLength > 0) {
			this.file.seek(fileLength);
		} else {
			this.buffer.store(bom, 0);
		}
	}

	/**
	 * Writes a string into the log file.
	 *
	 * @param content The string to write
	 * @throws IOException Failed to write into the log file
	 */
	public void write(String content) throws IOException {
		byte[] data = content.getBytes(charset);
		int bytes = buffer.store(data, bom.length);

		if (buffer.isFull()) {
			buffer.writeTo(file);
			buffer.reset(bufferCapacity);

			int remainingChunks = (data.length - bytes) / bufferCapacity;
			if (remainingChunks > 0) {
				int length = remainingChunks * bufferCapacity;
				file.write(data, bytes, length);
				bytes += length;
			}

			if (bytes < data.length) {
				buffer.store(data, bytes);
			}
		}
	}

	/**
	 * Writes all buffered data into the log file.
	 *
	 * @throws IOException Failed to write into the log file
	 */
	public void flush() throws IOException {
		if (!buffer.isEmpty()) {
			int remaining = buffer.writeTo(file);
			buffer.reset(remaining == 0 ? bufferCapacity : remaining);
		}
	}

	/**
	 * Closes the log file. All buffered data will be written before closing.
	 *
	 * @throws IOException Failed to write into the log file
	 */
	@Override
	public void close() throws IOException {
		try {
			flush();
		} finally {
			file.close();
		}
	}

	/**
	 * Creates the BOM for a charset.
	 *
	 * @param charset The charset for which the BOM should be created
	 * @return The BOM or an empty byte array if the passed charset does not have a BOM
	 */
	private static byte[] createBom(Charset charset) {
		byte[] singleSpace = " ".getBytes(charset);
		byte[] doubleSpaces = "  ".getBytes(charset);
		return Arrays.copyOf(doubleSpaces, singleSpace.length * 2 - doubleSpaces.length);
	}

}
