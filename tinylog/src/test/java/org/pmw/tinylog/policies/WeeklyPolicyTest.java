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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.pmw.tinylog.hamcrest.ClassMatchers.type;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.junit.Test;
import org.pmw.tinylog.util.FileHelper;

/**
 * Tests for weekly policy.
 * 
 * @see WeeklyPolicy
 */
public class WeeklyPolicyTest extends AbstractTimeBasedPolicyTest {

	/**
	 * Test rolling at the end of the week by default constructor.
	 */
	@Test
	public final void testDefaultRollingAtEndOfWeek() {
		// Thursday, 1st January 1970

		Locale.setDefault(Locale.US);
		Policy policy = new WeeklyPolicy();
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 3 - 1L); // Saturday, 3rd January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));
		
		setTime(0L); // Thursday, 1st January 1970

		Locale.setDefault(Locale.GERMANY);
		policy = new WeeklyPolicy();
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 4 - 1L); // Sunday, 4th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Monday, 5th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test rolling at the end of the week by setting explicitly 1.
	 */
	@Test
	public final void testRollingAtEndOfWeek() {
		Locale.setDefault(Locale.US);
		setTime(DAY); // Friday, 2nd January 1970
		
		Policy policy = new WeeklyPolicy(1);
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 2 - 1L); // Saturday, 3rd January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 7 - 1L); // Saturday, 10th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 11th January January 1970
		assertFalse(policy.check((String) null));
		
		Locale.setDefault(Locale.GERMANY);
		setTime(DAY); // Friday, 2nd January 1970
		
		policy = new WeeklyPolicy(1);
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 3 - 1L); // Sunday, 4th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Monday, 5th January 1970
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 7 - 1L); // Sunday, 11th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Monday, 12th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test rolling at Sunday 00:00 by setting explicitly "sunday".
	 */
	@Test
	public final void testRollingAtSundayMorning() {
		setTime(DAY); // Friday, 2nd January 1970

		Policy policy = new WeeklyPolicy("sunday");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 2 - 1L); // Saturday, 3rd January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));

		policy.reset();
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 7 - 1L); // Saturday, 10th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 11th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test exception for dayOfWeek = 0.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTooLowDay() {
		new WeeklyPolicy(0);
	}

	/**
	 * Test exception for dayOfWeek = 8.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTooHighDay() {
		new WeeklyPolicy(8);
	}

	/**
	 * Test String parameter for numeric Monday.
	 */
	@Test
	public final void testStringParameterForNumericMonday() {
		// Thursday, 1st January 1970

		Locale.setDefault(Locale.US);		
		Policy policy = new WeeklyPolicy("2");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 4 - 1L); // Sunday, 4th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Monday, 5th January 1970
		assertFalse(policy.check((String) null));
		
		setTime(0L); // Thursday, 1st January 1970

		Locale.setDefault(Locale.GERMANY);		
		policy = new WeeklyPolicy("1");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 4 - 1L); // Sunday, 4th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Monday, 5th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for numeric Sunday.
	 */
	@Test
	public final void testStringParameterForNumericSunday() {
		// Thursday, 1st January 1970

		Locale.setDefault(Locale.US);		
		Policy policy = new WeeklyPolicy("1");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 3 - 1L); // Saturday, 3th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));
		
		setTime(0L); // Thursday, 1st January 1970

		Locale.setDefault(Locale.GERMANY);		
		policy = new WeeklyPolicy("7");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 3 - 1L); // Saturday, 3th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "monday".
	 */
	@Test
	public final void testStringParameterForMonday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("monday");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 4 - 1L); // Sunday, 4th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Monday, 5th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "tuesday".
	 */
	@Test
	public final void testStringParameterForTuesday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("tuesday");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 5 - 1L); // Monday, 5th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Tuesday, 6th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "wednesday".
	 */
	@Test
	public final void testStringParameterForWednesday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("wednesday");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 6 - 1L); // Tuesday, 6th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Wednesday, 7th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "thursday".
	 */
	@Test
	public final void testStringParameterForThursday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("thursday");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 7 - 1L); // Wednesday, 7th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Thursday, 8th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "FRIDAY".
	 */
	@Test
	public final void testStringParameterForFriday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("FRIDAY");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY - 1L); // Thursday, 1st January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Friday, 2nd January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "Saturday".
	 */
	@Test
	public final void testStringParameterForSaturday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("Saturday");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 2 - 1L); // Friday, 2nd January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Saturday, 3rd January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test String parameter for "SunDaY".
	 */
	@Test
	public final void testStringParameterForSunday() {
		// Thursday, 1st January 1970

		Policy policy = new WeeklyPolicy("SunDaY");
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY * 3 - 1L); // Saturday, 3rd January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));
	}

	/**
	 * Test exception for "0".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidNumericDayOfWeek() {
		new WeeklyPolicy("0");
	}

	/**
	 * Test exception for "dummy".
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testStringParameterForInvalidString() {
		new WeeklyPolicy("dummy");
	}

	/**
	 * Test continuing log files.
	 * 
	 * @throws IOException
	 *             Test failed
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		setTime(DAY * 3 + DAY / 2L); // Sunday, 4th January 1970 12:00
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		Policy policy = new WeeklyPolicy("monday");
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(DAY / 2L - 1L); // Sunday, 4th January 1970 23:59:59,999
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970 24:00
		assertFalse(policy.check((String) null));

		increaseTime(-1L); // Sunday, 4th January 1970 23:59:59,999
		policy = new WeeklyPolicy("monday");
		policy.init(null);
		assertTrue(policy.check(file));
		assertTrue(policy.check((String) null));
		increaseTime(1L); // Sunday, 4th January 1970 24:00
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
		setTime(DAY * 3 + DAY / 2L); // Sunday, 4th January 1970 12:00
		File file = FileHelper.createTemporaryFile(null);
		file.setLastModified(getTime());

		WeeklyPolicy policy = new WeeklyPolicy("monday");
		policy.init(null);
		assertTrue(policy.check(file));

		increaseTime(DAY); // Monday, 5th January 1970 12:00

		policy = new WeeklyPolicy("monday");
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

		Policy policy = new WeeklyPolicy();
		policy.init(null);
		assertTrue(policy.check(file));
	}

	/**
	 * Test reading weekly policy from properties.
	 */
	@Test
	public final void testFromProperties() {
		 // Thursday, 1st January 1970
		
		Locale.setDefault(Locale.US);
		Policy policy = createFromProperties("weekly");
		assertThat(policy, type(WeeklyPolicy.class));
		policy.init(null);
		increaseTime(DAY * 2); // Saturday, 3rd January 1970
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));

		setTime(0L); // Thursday, 1st January 1970
		
		Locale.setDefault(Locale.GERMANY);
		policy = createFromProperties("weekly");
		assertThat(policy, type(WeeklyPolicy.class));
		policy.init(null);
		increaseTime(DAY * 3); // Sunday, 4th January 1970
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // Monday, 5th January 1970
		assertFalse(policy.check((String) null));

		setTime(0L); // Thursday, 1st January 1970

		policy = createFromProperties("weekly: friday");
		assertThat(policy, type(WeeklyPolicy.class));
		policy.init(null);
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // Friday, 2nd January 1970
		assertFalse(policy.check((String) null));

		setTime(0L); // Thursday, 1st January 1970

		policy = createFromProperties("weekly: sunday");
		assertThat(policy, type(WeeklyPolicy.class));
		policy.init(null);
		increaseTime(DAY * 2); // Saturday, 3rd January 1970
		assertTrue(policy.check((String) null));
		increaseTime(DAY); // Sunday, 4th January 1970
		assertFalse(policy.check((String) null));
	}

}
