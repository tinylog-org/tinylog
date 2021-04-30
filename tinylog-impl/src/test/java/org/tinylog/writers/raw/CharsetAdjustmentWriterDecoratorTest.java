/*
 * Copyright 2021 Martin Winandy
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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link CharsetAdjustmentWriterDecorator}.
 */
public class CharsetAdjustmentWriterDecoratorTest {

	/**
	 * Verifies that charset headers are detected and skipped correctly.
	 *
	 * @throws IOException
	 *             Failed invoking writer
	 */
	@Test
	public void writeDataWithHeader() throws IOException {
		byte[] charsetHeader = { 'A', 'B' };

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		CharsetAdjustmentWriterDecorator decorator = new CharsetAdjustmentWriterDecorator(writer, charsetHeader);

		decorator.write(new byte[] { 'A', 'B', 'C' }, 3);
		decorator.write(new byte[] { 'A', 'B', 'D', 'E' }, 4);
		decorator.write(new byte[] { 'Z', 'A', 'B', 'F' }, 1, 3);
		decorator.flush();
		decorator.close();

		assertThat(stream.toByteArray()).containsExactly('C', 'D', 'E', 'F');
	}

	/**
	 * Verifies that byte arrays without charset headers are written untouched.
	 *
	 * @throws IOException
	 *             Failed invoking writer
	 */
	@Test
	public void writeDataWithoutHeader() throws IOException {
		byte[] charsetHeader = { 'A', 'B' };

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(stream);
		CharsetAdjustmentWriterDecorator decorator = new CharsetAdjustmentWriterDecorator(writer, charsetHeader);

		decorator.write(new byte[] { 'C' }, 1);
		decorator.write(new byte[] { 'D', 'E', 'F' }, 3);
		decorator.write(new byte[] { 'A', 'B', 'C', 'D' }, 1, 3);
		decorator.flush();
		decorator.close();

		assertThat(stream.toByteArray()).containsExactly('C', 'D', 'E', 'F', 'B', 'C', 'D');
	}

}
