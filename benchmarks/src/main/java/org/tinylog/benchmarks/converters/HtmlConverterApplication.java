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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converter for creating diagrams from the results of the logging framework benchmark for tinylog.org.
 */
public final class HtmlConverterApplication {

	private static final String OUTPUT_FILE = "output.txt";
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
	private HtmlConverterApplication() {
	}

	/**
	 * Main method for executing the converter.
	 * 
	 * @param args
	 *            Arguments are ignored
	 */
	public static void main(final String[] args) {
		BenchmarkOutputParser parser = new BenchmarkOutputParser(FRAMEWORKS.keySet());
		HtmlDiagramRenderer renderer = new HtmlDiagramRenderer(FRAMEWORKS);
		renderer.output(parser.parse(OUTPUT_FILE));
	}

}
