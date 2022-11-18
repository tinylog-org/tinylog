package org.tinylog.impl.writers.file;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Buffered text file writer.
 */
public class LogFile implements Closeable {

    private final RandomAccessFile file;
    private final boolean newFile;

    private final int bufferCapacity;
    private final ByteBuffer buffer;

    /**
     * @param path The path to the log file
     * @param bufferCapacity The capacity for the byte buffer (must be greater than 0)
     * @param append {@code true} for appending an already existing file, {@code false} for overwriting an already
     *               existing file
     * @throws IOException Failed to open the log file
     */
    public LogFile(Path path, int bufferCapacity, boolean append) throws IOException {
        if (!append) {
            Files.deleteIfExists(path);
        }

        this.file = new RandomAccessFile(path.toFile(), "rw");

        long fileLength = this.file.length();
        long maxBufferSize = bufferCapacity - (fileLength % bufferCapacity);

        this.buffer = new ByteBuffer(bufferCapacity, (int) maxBufferSize);
        this.bufferCapacity = bufferCapacity;

        this.newFile = fileLength == 0;
        this.file.seek(fileLength);
    }

    /**
     * Checks if this log file has started as a new empty file or is appending an already existing file.
     *
     * @return {@code true} if this log file has started as a new empty file or {@code false} if it is appending an
     *         already existing file
     */
    public boolean isNewFile() {
        return newFile;
    }

    /**
     * Writes a byte array into the log file.
     *
     * @param data The bytes to write
     * @param start The starting position in the passed source byte array
     * @throws IOException Failed to write into the log file
     */
    public void write(byte[] data, int start) throws IOException {
        int bytes = buffer.store(data, start);

        if (buffer.isFull()) {
            buffer.writeTo(file);
            buffer.reset(bufferCapacity);

            int remainingChunks = (data.length - start - bytes) / bufferCapacity;
            if (remainingChunks > 0) {
                int length = remainingChunks * bufferCapacity;
                file.write(data, bytes, length);
                bytes += length;
            }

            if (bytes < data.length - start) {
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

}
