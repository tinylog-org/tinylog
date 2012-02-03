package org.pmw.tinylog;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests old fixed bugs to prevent regressions.
 */
public class RegressionsTest {

	private static final String NEW_LINE = System.getProperty("line.separator");

	/**
	 * Bug: Wrong class in log entry if there isn't set any special logging level for at least one package.
	 */
	@Test
	public final void wrongClassTest() {
		LoggingWriter writer = new LoggingWriter();
		Logger.setWriter(writer);
		Logger.setLoggingLevel(ELoggingLevel.TRACE);
		Logger.setLoggingFormat("{class}");

		Logger.setLoggingLevel("org", ELoggingLevel.TRACE);
		Logger.info("");
		assertEquals(RegressionsTest.class.getName() + NEW_LINE, writer.consumeMessage()); // Was already OK
		Logger.resetLoggingLevel("org");

		Logger.info("");
		assertEquals(RegressionsTest.class.getName() + NEW_LINE, writer.consumeMessage()); // Failed
	}

}
