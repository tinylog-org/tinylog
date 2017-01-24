/*
 * Copyright 2016 Martin Winandy
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

package org.tinylog.writers.raw;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Tests for {@link OutputStreamWriter}.
 */
public final class OutputStreamWriterTest {

	/**
	 * Verifies that a {@link OutputStream} is wrapped correctly.
	 *
	 * @throws IOException
	 *             Failed invoking writer
	 */
	@Test
	public void test() throws IOException {
		ByteArrayOutputStream stream = spy(new ByteArrayOutputStream());
		OutputStreamWriter writer = new OutputStreamWriter(stream);

		writer.write(new byte[] { 1, 2, 3 }, 2);
		writer.flush();
		writer.close();

		assertThat(stream.toByteArray()).containsExactly((byte) 1, (byte) 2);
		verify(stream, times(1)).close();
	}

}
