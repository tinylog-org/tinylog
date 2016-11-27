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

package org.tinylog.runtime;

import android.os.Process;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link AndroidRuntime}.
 */
@RunWith(PowerMockRunner.class)
public final class AndroidRuntimeTest {

	/**
	 * Verifies that the process ID will be returned.
	 */
	@Test
	@PrepareForTest(Process.class)
	public void processId() {
		mockStatic(Process.class);
		when(Process.myPid()).thenReturn(123);

		assertThat(new AndroidRuntime().getProcessId()).isEqualTo(123);
	}

}
