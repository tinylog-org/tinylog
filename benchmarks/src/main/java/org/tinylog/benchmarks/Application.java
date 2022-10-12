/*
 * Copyright 2022 Martin Winandy
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

package org.tinylog.benchmarks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openjdk.jmh.Main;
import org.tinylog.Logger;

/**
 * Application wrapper for executing the JMH benchmarks and transforming the output CSV file for the website.
 */
public final class Application {

	private static final String FILE_OPTION = "-rff";

	private static final Map<String, Framework> FRAMEWORKS = new LinkedHashMap<>();

	static {
		FRAMEWORKS.put("Tinylog2", new Framework("tinylog", "with writing thread", org.tinylog.Logger.class));
		FRAMEWORKS.put("Tinylog1", new Framework("tinylog", "with writing thread", org.pmw.tinylog.Logger.class));
		FRAMEWORKS.put("Log4j2", new Framework("Log4j", "with async logger", org.apache.logging.log4j.Logger.class));
		FRAMEWORKS.put("Log4j1", new Framework("Log4j", "with async appender", org.apache.log4j.Logger.class));
		FRAMEWORKS.put("Logback", new Framework("Logback", "with async appender", ch.qos.logback.classic.Logger.class));
		FRAMEWORKS.put("Jul", new Framework("java.util.logging"));
		FRAMEWORKS.put("NoOp", new Framework("Empty Method"));
	}

	/** */
	private Application() {
	}

	/**
	 * Executes JMH and transforms the output CSV file.
	 *
	 * @param args
	 *            The arguments for JMH
	 * @throws IOException
	 *            Failed to execute JMH or to access the output CSV file
	 */
	public static void main(final String[] args) throws IOException {
		Main.main(args);

		Optional<String> fileName = Arrays.stream(args)
				.dropWhile(argument -> !FILE_OPTION.equals(argument))
				.skip(1)
				.findFirst();

		if (fileName.isPresent()) {
			Path path = Paths.get(fileName.get());
			List<String> input = Files.readAllLines(path);

			if (input.isEmpty()) {
				Logger.warn("CSV file \"{}\" is empty", path);
			} else {
				List<String> output = transformCsvFile(input);
				Files.write(path, output);
			}
		}
	}

	/**
	 * Transforms the data of the output CSV file for the website.
	 *
	 * <p>
	 *     The benchmark class name will be replaced with the human-readable name and version number of the logging
	 *     framework. For asynchronous benchmarks, the name of the asynchronous technique will be appended.
	 * </p>
	 *
	 * @param input
	 *            All lines of the original output CSV file
	 * @return The transformed CSV file lines for the website
	 */
	private static List<String> transformCsvFile(final List<String> input) {
		List<String> output = new ArrayList<>();
		output.add(input.remove(0));

		for (Map.Entry<String, Framework> entry : FRAMEWORKS.entrySet()) {
			String name = entry.getKey();
			Framework framework = entry.getValue();

			Pattern namePattern = Pattern.compile("[^\"]*" + Pattern.quote(name) + "_*Benchmark\\.([^\"]+)");
			Pattern asyncPattern = Pattern.compile(",true,[A-Z_]+$");

			Iterator<String> iterator = input.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				Matcher matcher = namePattern.matcher(line);
				if (matcher.find()) {
					boolean async = asyncPattern.matcher(line).find();
					output.add(matcher.replaceFirst(framework.getName(async) + " / $1"));
					iterator.remove();
				}
			}
		}

		output.addAll(input);
		return output;
	}

}
