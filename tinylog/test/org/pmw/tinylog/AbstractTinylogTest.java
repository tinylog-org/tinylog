/*
 * Copyright 2012 Martin Winandy
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

package org.pmw.tinylog;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.pmw.tinylog.writers.VMShutdownHook;
import org.pmw.tinylog.writers.Writer;

/**
 * Base class for all tinylog tests.
 */
public abstract class AbstractTinylogTest extends AbstractCoreTest {

	/**
	 * Load and activate tinylog's default configuration.
	 */
	@Before
	public final void loadConfiguration() {
		Configurator.defaultConfig().activate();
	}

	/**
	 * Clear thread-based mapped logging context.
	 */
	@After
	public final void clearLoggingContext() {
		LoggingContext.clear();
	}

	/**
	 * Close all open writers.
	 */
	@After
	public final void closeWriters() {
		try {
			Collection<Writer> openWriters = getOpenWriters();
			for (Writer writer : openWriters) {
				try {
					writer.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			assertThat("All writers must be closed", openWriters, empty());
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Writer> getOpenWriters() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = VMShutdownHook.class.getDeclaredField("writers");
		field.setAccessible(true);
		return new ArrayList<Writer>((Collection<Writer>) field.get(null));
	}

}
