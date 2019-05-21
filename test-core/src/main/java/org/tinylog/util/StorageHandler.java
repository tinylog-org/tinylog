/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Simple handler for {@code java.util.logging} that just stores all {@link LogRecord} in a list.
 */
public class StorageHandler extends Handler {

	private final List<LogRecord> records;

	/** */
	public StorageHandler() {
		records = new ArrayList<>();
	}

	/**
	 * Gets a copy of all received log records.
	 * 
	 * @return Received log records
	 */
	public List<LogRecord> getRecords() {
		return new ArrayList<LogRecord>(records);
	}

	@Override
	public void publish(final LogRecord record) {
		record.getSourceClassName(); // Ensure that caller is resolved properly
		records.add(record);
	}

	@Override
	public void flush() {
		// Ignore
	}

	@Override
	public void close() {
		records.clear();
	}

}
