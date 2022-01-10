package org.tinylog.impl.writers.output;

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
class ByteBufferTest {

	@Mock
	private DataOutput output;

	/**
	 * Verifies that multiple small byte arrays can be stored in the same byte buffer.
	 */
	@Test
	void storeMultipleValues() throws IOException {
		ByteBuffer buffer = new ByteBuffer(8, 8);

		int first = buffer.store(new byte[] {0, 1, 2, 3}, 0);
		assertThat(first).isEqualTo(4);

		int second = buffer.store(new byte[] {4, 5}, 0);
		assertThat(second).isEqualTo(2);

		int remaining = buffer.writeTo(output);
		assertThat(remaining).isEqualTo(2);

		verify(output).write(eq(new byte[] {0, 1, 2, 3, 4, 5, 0, 0}), eq(0), eq(6));
	}

	/**
	 * Verifies that a big byte array can be stored partly in a byte buffer.
	 */
	@Test
	void storePartialValue() throws IOException {
		ByteBuffer buffer = new ByteBuffer(2, 2);

		int count = buffer.store(new byte[] {0, 1, 2, 3}, 1);
		assertThat(count).isEqualTo(2);

		int remaining = buffer.writeTo(output);
		assertThat(remaining).isEqualTo(0);

		verify(output).write(eq(new byte[] {1, 2}), eq(0), eq(2));
	}

	/**
	 * Verifies that the empty getter works correctly.
	 */
	@Test
	void isEmpty() {
		ByteBuffer buffer = new ByteBuffer(2, 2);
		assertThat(buffer.isEmpty()).isTrue();

		buffer.store(new byte[] {1}, 0);
		assertThat(buffer.isEmpty()).isFalse();

		buffer.reset(2);
		assertThat(buffer.isEmpty()).isTrue();
	}

	/**
	 * Verifies that the full getter works correctly.
	 */
	@Test
	void isFull() {
		ByteBuffer buffer = new ByteBuffer(2, 2);
		assertThat(buffer.isFull()).isFalse();

		buffer.store(new byte[] {1}, 0);
		assertThat(buffer.isFull()).isFalse();

		buffer.store(new byte[] {2}, 0);
		assertThat(buffer.isFull()).isTrue();

		buffer.reset(2);
		assertThat(buffer.isFull()).isFalse();
	}

	/**
	 * Verifies that the maximum size can be changed via reset.
	 */
	@Test
	void resetMaxSize() {
		ByteBuffer buffer = new ByteBuffer(4, 2);

		int first = buffer.store(new byte[] {0, 1, 2, 3}, 0);
		assertThat(first).isEqualTo(2);

		buffer.reset(3);

		int second = buffer.store(new byte[] {0, 1, 2, 3}, 0);
		assertThat(second).isEqualTo(3);
	}

}
