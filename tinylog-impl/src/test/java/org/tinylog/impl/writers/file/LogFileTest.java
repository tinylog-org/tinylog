package org.tinylog.impl.writers.file;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogFileTest {

	private Path path;

	/**
	 * Creates a temporary log file.
	 *
	 * @throws IOException Failed to create a temporary log file
	 */
	@BeforeEach
	void init() throws IOException {
		path = Files.createTempFile("tinylog", ".log");
		path.toFile().deleteOnExit();
	}

	/**
	 * Deletes the created temporary log file.
	 *
	 * @throws IOException Failed to delete the temporary log file
	 */
	@AfterEach
	void release() throws IOException {
		Files.deleteIfExists(path);
	}

	/**
	 * Verifies that an existing log file can be overwritten.
	 */
	@Test
	void overwriteLogFile() throws IOException {
		try (LogFile file = new LogFile(path, 64, false)) {
			assertThat(file.isNewFile()).isTrue();
			file.write(new byte[] {'f', 'o', 'o'}, 0);
		}

		try (LogFile file = new LogFile(path, 64, false)) {
			assertThat(file.isNewFile()).isTrue();
			file.write(new byte[] {'b', 'a', 'r'}, 0);
		}

		assertThat(path).usingCharset(StandardCharsets.US_ASCII).hasContent("bar");
	}

	/**
	 * Verifies that an existing log file can be continued.
	 */
	@Test
	void continueLogFile() throws IOException {
		try (LogFile file = new LogFile(path, 64, true)) {
			assertThat(file.isNewFile()).isTrue();
			file.write(new byte[] {'f', 'o', 'o'}, 0);
		}

		try (LogFile file = new LogFile(path, 64, true)) {
			assertThat(file.isNewFile()).isFalse();
			file.write(new byte[] {'b', 'a', 'r'}, 0);
		}

		assertThat(path).usingCharset(StandardCharsets.US_ASCII).hasContent("foobar");
	}

	/**
	 * Verifies that the first bytes of a byte array can be omitted from writing.
	 */
	@Test
	void writingPartially() throws IOException {
		try (LogFile file = new LogFile(path, 8, true)) {
			file.write(new byte[] {'f', 'o', 'o'}, 1);
		}

		assertThat(path).usingCharset(StandardCharsets.US_ASCII).hasContent("oo");
	}

	/**
	 * Verifies that the buffer will be written into the file when the buffer is full.
	 */
	@Test
	void writingUntilBufferCapacity() throws IOException {
		Files.write(path, new byte[6]);

		try (LogFile file = new LogFile(path, 8, true)) {
			assertThat(path).hasSize(6);
			file.write(new byte[1], 0);

			assertThat(path).hasSize(6);
			file.write(new byte[1], 0);

			assertThat(path).hasSize(8);
		}
	}

	/**
	 * Verifies that byte arrays can be written, even if they are larger than the buffer size.
	 */
	@Test
	void writingOversizedByteArray() throws IOException {
		try (LogFile file = new LogFile(path, 8, false)) {
			file.write(new byte[16 + 1], 0);
			assertThat(path).hasSize(16);
		}

		assertThat(path).hasSize(16 + 1);
	}

	/**
	 * Verifies that the buffer will be written into the file when flushing.
	 */
	@Test
	void flushing() throws IOException {
		try (LogFile file = new LogFile(path, 64, false)) {
			file.write(new byte[] {'f', 'o', 'o'}, 0);
			assertThat(path).isEmptyFile();

			file.flush();
			assertThat(path).usingCharset(StandardCharsets.US_ASCII).hasContent("foo");
		}
	}

}
