package org.tinylog.impl.backend;

import org.junit.jupiter.api.Test;
import org.tinylog.impl.writers.AsyncWriter;
import org.tinylog.impl.writers.Writer;

import com.google.common.collect.ImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class WriterRepositoryTest {

	/**
	 * Verifies that sync and async writers are partitioning correctly.
	 */
	@Test
	void partitioningOfWriters() {
		Writer first = mock(Writer.class);
		AsyncWriter second = mock(AsyncWriter.class);
		Writer third = mock(Writer.class);

		WriterRepository repository = new WriterRepository(ImmutableList.of(first, second, third));
		assertThat(repository.getAllWriters()).containsExactlyInAnyOrder(first, second, third);
		assertThat(repository.getSyncWriters()).containsExactlyInAnyOrder(first, third);
		assertThat(repository.getAsyncWriters()).containsExactlyInAnyOrder(second);
	}

}
