/*
 * Copyright 2018 Martin Winandy
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

package org.tinylog.benchmarks.converters;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tinylog.Logger;

/**
 * Parser for parsing the results of the logging framework benchmark from JMH.
 */
public final class BenchmarkOutputParser {

	private static final Pattern LINE_PATTERN = Pattern.compile("(\\w+)Benchmark\\.(\\w+) +[\\w/]+ +\\w+ +\\d* +(\\d+[,\\.]\\d+) .*");

	private static final int GROUP_FRAMEWORK_NAME = 1;
	private static final int GROUP_BENCHMARK_NAME = 2;
	private static final int GROUP_SCORE_VALUE = 3;

	private final Collection<String> frameworks;

	/**
	 * @param frameworks
	 *            Internal name of all supported logging frameworks (the internal name is the class name without the
	 *            "Benchmark" postfix)
	 */
	public BenchmarkOutputParser(final Collection<String> frameworks) {
		this.frameworks = frameworks;
	}

	/**
	 * Parses the given file .
	 * 
	 * @param file
	 *            Results of the logging framework benchmark from JMH
	 * @return Mapping with the benchmark name, the internal framework name and its scores
	 */
	public Map<String, Map<String, List<BigDecimal>>> parse(final String file) {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(openResource(file), StandardCharsets.UTF_8))) {
			Map<String, Map<String, List<BigDecimal>>> benchmarks = new TreeMap<>(Collections.reverseOrder());

			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				Matcher matcher = LINE_PATTERN.matcher(line);
				if (matcher.matches()) {
					String framework = matcher.group(GROUP_FRAMEWORK_NAME);
					String benchmark = matcher.group(GROUP_BENCHMARK_NAME);
					String score = matcher.group(GROUP_SCORE_VALUE);

					if (!frameworks.contains(framework)) {
						Logger.error("Unknown logging framework: \"{}\"", framework);
						return Collections.emptyMap();
					}

					BigDecimal parsedScore = new BigDecimal(score.replaceAll("[,\\.]", "."));

					Map<String, List<BigDecimal>> benchmarkEntry = benchmarks.get(benchmark);
					if (benchmarkEntry == null) {
						benchmarkEntry = new LinkedHashMap<>();
						benchmarks.put(benchmark, benchmarkEntry);
					}

					List<BigDecimal> scores = benchmarkEntry.get(framework);
					if (scores == null) {
						scores = new ArrayList<>();
						benchmarkEntry.put(framework, scores);
					}

					scores.add(parsedScore);
				}
			}

			return benchmarks;
		} catch (IOException ex) {
			Logger.error(ex, "Failed to open \"{}\"", file);
			return Collections.emptyMap();
		}
	}

	/**
	 * Opens a resource from classpath.
	 * 
	 * @param file
	 *            Filename of resource
	 * @return Stream of given file
	 * @throws IOException
	 *             Failed to open resource
	 */
	private static InputStream openResource(final String file) throws IOException {
		InputStream stream = HtmlConverterApplication.class.getResourceAsStream("/" + file);
		if (stream == null) {
			throw new FileNotFoundException(file);
		} else {
			return stream;
		}
	}

}
