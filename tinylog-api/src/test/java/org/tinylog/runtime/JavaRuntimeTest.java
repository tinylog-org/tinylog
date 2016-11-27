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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.tinylog.rules.SystemStreamCollector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link JavaRuntime}.
 */
@RunWith(PowerMockRunner.class)
public final class JavaRuntimeTest {

	/**
	 * Redirects and collects system output streams.
	 */
	@Rule
	public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

	/**
	 * Verifies that the process ID will be returned.
	 */
	@Test
	public void processId() {
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		assertThat(new JavaRuntime().getProcessId()).isEqualTo(Integer.parseInt(pid));
	}

	/**
	 * Verifies that there is a fallback, if {@link RuntimeMXBean#getName()} doesn't contain the expected '@' character.
	 */
	@Test
	@PrepareForTest(JavaRuntime.class)
	public void invalidJvmName() {
		RuntimeMXBean bean = mock(RuntimeMXBean.class);
		when(bean.getName()).thenReturn("test");

		mockStatic(ManagementFactory.class);
		when(ManagementFactory.getRuntimeMXBean()).thenReturn(bean);

		assertThat(new JavaRuntime().getProcessId()).isEqualTo(-1);
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("test");
	}

	/**
	 * Verifies that there is a fallback, if {@link RuntimeMXBean#getName()} doesn't contain a numeric process ID (pid).
	 */
	@Test
	@PrepareForTest(JavaRuntime.class)
	public void invalidProcessId() {
		RuntimeMXBean bean = mock(RuntimeMXBean.class);
		when(bean.getName()).thenReturn("abc@test");

		mockStatic(ManagementFactory.class);
		when(ManagementFactory.getRuntimeMXBean()).thenReturn(bean);

		assertThat(new JavaRuntime().getProcessId()).isEqualTo(-1);
		assertThat(systemStream.consumeErrorOutput()).containsOnlyOnce("ERROR").containsOnlyOnce("abc");
	}

}
