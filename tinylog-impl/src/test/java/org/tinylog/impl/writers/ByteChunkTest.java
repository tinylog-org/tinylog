package org.tinylog.impl.writers;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ByteChunkTest {

	@Mock
	private DataOutput output;

	/**
	 * Verifies that multiple small byte arrays can be stored in the same byte chunk.
	 */
	@Test
	void storeMultipleValues() throws IOException {
		ByteChunk chunk = new ByteChunk(8, 8);

		int first = chunk.store(new byte[] {0, 1, 2, 3}, 0);
		assertThat(first).isEqualTo(4);

		int second = chunk.store(new byte[] {4, 5}, 0);
		assertThat(second).isEqualTo(2);

		int total = chunk.writeTo(output);
		assertThat(total).isEqualTo(6);

		verify(output).write(eq(new byte[] {0, 1, 2, 3, 4, 5, 0, 0}), eq(0), eq(6));
	}

	/**
	 * Verifies that a big byte array can be stored partly in a byte chunk.
	 */
	@Test
	void storePartialValue() throws IOException {
		ByteChunk chunk = new ByteChunk(2, 2);

		int count = chunk.store(new byte[] {0, 1, 2, 3}, 1);
		assertThat(count).isEqualTo(2);

		int total = chunk.writeTo(output);
		assertThat(total).isEqualTo(2);

		verify(output).write(eq(new byte[] {1, 2}), eq(0), eq(2));
	}

	/**
	 * Verifies that the empty getter works correctly.
	 */
	@Test
	void isEmpty() {
		ByteChunk chunk = new ByteChunk(2, 2);
		assertThat(chunk.isEmpty()).isTrue();

		chunk.store(new byte[] {1}, 0);
		assertThat(chunk.isEmpty()).isFalse();

		chunk.reset(2);
		assertThat(chunk.isEmpty()).isTrue();
	}

	/**
	 * Verifies that the full getter works correctly.
	 */
	@Test
	void isFull() {
		ByteChunk chunk = new ByteChunk(2, 2);
		assertThat(chunk.isFull()).isFalse();

		chunk.store(new byte[] {1}, 0);
		assertThat(chunk.isFull()).isFalse();

		chunk.store(new byte[] {2}, 0);
		assertThat(chunk.isFull()).isTrue();

		chunk.reset(2);
		assertThat(chunk.isFull()).isFalse();
	}

	/**
	 * Verifies that the maximum size can be changed via reset.
	 */
	@Test
	void resetMaxSize() {
		ByteChunk chunk = new ByteChunk(4, 2);

		int first = chunk.store(new byte[] {0, 1, 2, 3}, 0);
		assertThat(first).isEqualTo(2);

		chunk.reset(3);

		int second = chunk.store(new byte[] {0, 1, 2, 3}, 0);
		assertThat(second).isEqualTo(3);
	}

}
