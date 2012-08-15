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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.policies.SizePolicy;
import org.pmw.tinylog.util.StoreWriter;
import org.pmw.tinylog.writers.RollingFileWriter;

/**
 * Tests old fixed bugs to prevent regressions.
 */
public class RegressionsTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Bug: Wrong class in log entry if there isn't set any special logging level for at least one package.
	 */
	@Test
	public final void testWrongClass() {
		StoreWriter writer = new StoreWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(LoggingLevel.TRACE);
		Logger.setLoggingFormat("{class}");

		Logger.setLoggingLevel("org", LoggingLevel.TRACE);
		Logger.info("");
		assertEquals(RegressionsTest.class.getName() + NEW_LINE, writer.consumeMessage()); // Was already OK
		Logger.resetLoggingLevel("org");

		Logger.info("");
		assertEquals(RegressionsTest.class.getName() + NEW_LINE, writer.consumeMessage()); // Failed
	}

	/**
	 * Bug: If a log file is continued, the policy will start from scratch. This leads to a too late rollover.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		File file = File.createTempFile("test", "tmp");
		file.deleteOnExit();

		RollingFileWriter writer = new RollingFileWriter(file.getAbsolutePath(), 0, new SizePolicy(10));
		writer.write(null, "12345");
		writer.close();

		writer = new RollingFileWriter(file.getAbsolutePath(), 0, new SizePolicy(10));
		writer.write(null, "123456");
		writer.close();

		assertEquals(6, file.length());
		file.delete();
	}

}
