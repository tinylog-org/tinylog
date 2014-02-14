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

package org.pmw.tinylog.policies;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for yearly policy.
 * 
 * @see YearlyPolicy
 */
public class YearlyPolicyTest extends AbstractTimeBasedPolicyTest {

	/**
	 * Test rolling at the end of the year by default constructor.
	 */
	@Test
	public final void testDefaultRollingAtEndOfYear() {
		// 1st January 1970

		Policy policy = new YearlyPolicy();
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - DAY); // 31th December 1970
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // 1st January 1971
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test rolling at the end of the year by setting explicitly 1.
	 */
	@Test
	public final void testRollingAtEndOfYear() {
		increaseTime(YEAR - DAY); // 31th December 1970

		Policy policy = new YearlyPolicy(1);
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY - 1L); // 31th December 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // / 1st January 1971
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - 1L); // 31th December 1971 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st January 1972
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test rolling at the end of December by setting explicitly 12.
	 */
	@Test
	public final void testRollingAtEndOfDecember() {
		increaseTime(YEAR - 31 * DAY - DAY); // 30th November 1970

		Policy policy = new YearlyPolicy(12);
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY - 1L); // 30th November 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // / 1st December 1970
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - 1L); // 30th November 1971 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st December 1971
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test exception for month = 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTooLowMonth() {
		new YearlyPolicy(0);
	}

	/**
	 * Test exception for month = 13.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTooHighMonth() {
		new YearlyPolicy(13);
	}

	/**
	 * Test String parameter for numeric January (= "1").
	 */
	@Test
	public final void testStringParameterForNumericJanuary() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("1");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - 1L); // 31th December 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st January 1971
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for numeric December (= "12").
	 */
	@Test
	public final void testStringParameterForNumericDecember() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("12");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - 31 * DAY - 1L); // 30th November 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st December 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "january".
	 */
	@Test
	public final void testStringParameterForJanuary() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("january");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - 1L); // 31th December 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st January 1971
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "february".
	 */
	@Test
	public final void testStringParameterForFebruary() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("february");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 31 - 1L); // 31th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st February 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "march".
	 */
	@Test
	public final void testStringParameterForMarch() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("march");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28) - 1L); // 28th February 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st March 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "april".
	 */
	@Test
	public final void testStringParameterForApril() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("april");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31) - 1L); // 31th March 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st April 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "may".
	 */
	@Test
	public final void testStringParameterForMay() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("may");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30) - 1L); // 30th April 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st May 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "june".
	 */
	@Test
	public final void testStringParameterForJune() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("june");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30 + 31) - 1L); // 31th May 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st June 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "july".
	 */
	@Test
	public final void testStringParameterForJuly() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("july");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30 + 31 + 30) - 1L); // 30th June 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st July 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "august".
	 */
	@Test
	public final void testStringParameterForAugust() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("august");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30 + 31 + 30 + 31) - 1L); // 31th July 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st August 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "september".
	 */
	@Test
	public final void testStringParameterForSeptember() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("september");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30 + 31 + 30 + 31 + 31) - 1L); // 31th August 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st September 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "OCTOBER".
	 */
	@Test
	public final void testStringParameterForOctober() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("OCTOBER");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30) - 1L); // 30th September 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st October 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "NovemBer".
	 */
	@Test
	public final void testStringParameterForNovember() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("NovemBer");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * (31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31) - 1L); // 31th October 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st November 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "December".
	 */
	@Test
	public final void testStringParameterForDecember() {
		// 1st January 1970

		Policy policy = new YearlyPolicy("December");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(YEAR - 31 * DAY - 1L); // 30th November 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st December 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test exception for "0".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidNumericMonth() {
		new YearlyPolicy("0");
	}

	/**
	 * Test exception for "dummy".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidString() {
		new YearlyPolicy("dummy");
	}

	/**
	 * Test continuing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		setTime(YEAR - DAY); // 31th December 1970 00:00
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new YearlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(DAY - 1L); // 31th December 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st January 1971 00:00
		assertFalse(policy.check((String) null));

		increaseTime(-1L); // 31th December 1970 23:59:59,999
		policy = new YearlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(1L); // 1st January 1971 00:00
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test discontinuing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testDiscontinueLogFile() throws IOException {
		setTime(YEAR - DAY); // 31th December 1970
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new YearlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));

		increaseTime(DAY); // 1st January 1971

		policy = new YearlyPolicy();
		policy.init(null);
		assertFalse(policy.check(file));

		file.delete();
	}

	/**
	 * Test non-existing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testNonExistingLogFile() throws IOException {
		File file = FileHelper.createTemporaryFile(null);
		file.delete();

		Policy policy = new YearlyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
	}

	/**
	 * Test reading yearly policy from properties.
	 */
	@Test
	public final void testFromProperties() {
		Policy policy = createFromProperties("yearly");
		assertNotNull(policy);
		assertEquals(YearlyPolicy.class, policy.getClass());
		policy.init(null);
		increaseTime(YEAR - DAY);
		assertTrue(policy.check((String) null));
		increaseTime(DAY);
		assertFalse(policy.check((String) null));

		setTime(0L);

		policy = createFromProperties("yearly: 2");
		assertNotNull(policy);
		assertEquals(YearlyPolicy.class, policy.getClass());
		policy.init(null);
		increaseTime(DAY * 30); // 31th January
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // 1st February
		assertFalse(policy.check((String) null));

		setTime(0L);

		policy = createFromProperties("yearly: march");
		assertNotNull(policy);
		assertEquals(YearlyPolicy.class, policy.getClass());
		policy.init(null);
		increaseTime(DAY * 30 + DAY * 28); // 28th February
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // 1st March
		assertFalse(policy.check((String) null));
	}

}
