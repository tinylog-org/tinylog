/*
 * Copyright 2020 Martin Winandy
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

package org.tinylog.converters;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link NopFileConverter}.
 */
public class NopFileConverterTest {

	/**
	 * Temporary folder for creating files.
	 */
	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	/**
	 * Verifies that there is no custom backup file extension.
	 */
	@Test
	public void suffix() {
		assertThat(new NopFileConverter().getBackupSuffix()).isNull();
	}

	/**
	 * Verifies that data won't be modified.
	 *
	 * @throws IOException
	 *             Failed to create new file
	 */
	@Test
	public void conversion() throws IOException {
		String fileName = folder.newFile().getAbsolutePath();
		byte[] data = new byte[] { 0, 1, 2, 3 };
		NopFileConverter converter = new NopFileConverter();

		converter.open(fileName);
		assertThat(converter.write(data)).isSameAs(data);
		converter.close();
	}

}
