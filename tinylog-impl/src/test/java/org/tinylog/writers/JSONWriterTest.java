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

package org.tinylog.writers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.tinylog.rules.SystemStreamCollector;
import org.tinylog.util.FileSystem;
import org.tinylog.util.LogEntryBuilder;

public final class JSONWriterTest {

    private static final String NEW_LINE = System.lineSeparator();

    /**
     * Redirects and collects system output streams.
     */
    @Rule
    public final SystemStreamCollector systemStream = new SystemStreamCollector(true);

    /**
     * Verifies that log entries will be immediately output, if buffer is disabled.
     *
     * @throws IOException Failed writing to file
     */
    @Test
    public void unbufferedWriting() throws IOException {
        // String file = FileSystem.createTemporaryFile();
        File myObj = new File("logfile.json");
        myObj.createNewFile();
        String file = myObj.getName();

        Map<String, String> properties = new HashMap<>();
        properties.put("file", file);
        properties.put("buffered", "false");
        properties.put("append", "false");
        JSONWriter writer;
        writer = new JSONWriter(properties);
        writer.write(LogEntryBuilder.prefilled(JSONWriterTest.class).create());
        writer.write(LogEntryBuilder.prefilled(JSONWriterTest.class).create());
        writer.close();
        writer = new JSONWriter(properties);
        writer.write(LogEntryBuilder.prefilled(JSONWriterTest.class).create());
        writer.write(LogEntryBuilder.prefilled(JSONWriterTest.class).create());
        writer.close();

        String resultingText1 = FileSystem.readFile(file);

        assertThat(resultingText1).contains("1985").contains("03").contains("TRACE").contains("Hello World!");
    }

}
