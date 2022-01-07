package org.tinylog.impl.writers;

import java.io.DataOutput;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Byte array buffer for {@link FileWriter}.
 */
public final class ByteBuffer {

	private final byte[] data;
	private int maxSize;
	private int currentSize;

	/**
	 * @param capacity The capacity for the internal byte array
	 * @param maxSize The initial maximum number of bytes to accept
	 */
	public ByteBuffer(int capacity, int maxSize) {
		this.data = new byte[capacity];
		this.maxSize = maxSize;
		this.currentSize = 0;
	}

	/**
	 * Checks if the byte buffer is empty.
	 *
	 * @return {@code true} if the byte buffer is empty, {@code false} if one or more bytes are stored
	 */
	public boolean isEmpty() {
		return currentSize == 0;
	}

	/**
	 * Checks if the byte buffer is full.
	 *
	 * <p>
	 *     A byte buffer can store as many bytes as defined by the current maximum size.
	 * </p>
	 *
	 * @return {@code true} if the byte buffer is full, {@code false} if there is still available space for one or more
	 *         bytes
	 */
	public boolean isFull() {
		return currentSize == maxSize;
	}

	/**
	 * Copies bytes from a passed byte array into this byte buffer.
	 *
	 * <p>
	 *     This method will copy the bytes from the passed byte array, starting at the passed position, until either all
	 *     bytes are copied or this byte buffer is full.
	 * </p>
	 *
	 * @param data The source byte array
	 * @param start The starting position in the passed source byte array
	 * @return The number of copied bytes
	 */
	public int store(byte[] data, int start) {
		int length = Math.min(data.length - start, maxSize - currentSize);
		System.arraycopy(data, start, this.data, currentSize, length);
		currentSize += length;
		return length;
	}

	/**
	 * Writes all stored bytes to any {@link DataOutput} (e.g. {@link RandomAccessFile}).
	 *
	 * @param output The destination for writing the stored bytes
	 * @return The number of written bytes
	 * @throws IOException Failed to write to the passed data output
	 */
	public int writeTo(DataOutput output) throws IOException {
		output.write(data, 0, currentSize);
		return currentSize;
	}

	/**
	 * Resets the data buffer for making it ready for new content.
	 *
	 * @param maxSize The new maximum number of bytes to accept
	 */
	public void reset(int maxSize) {
		this.maxSize = maxSize;
		this.currentSize = 0;
	}

}
