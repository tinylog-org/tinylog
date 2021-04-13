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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.tinylog.benchmarks.logging.LocationInfo;

/**
 * Renderer for creating HTML diagrams from results of logging framework benchmarks for tinylog.org.
 */
public final class HtmlDiagramRenderer {

	private static final BigDecimal PERCENTAGE = BigDecimal.valueOf(100);
	private static final int DECIMAL_PLACES = 2;
	private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.ENGLISH);

	private final Map<String, Framework> frameworks;

	/**
	 * @param frameworks
	 *            All supported logging frameworks
	 */
	public HtmlDiagramRenderer(final Map<String, Framework> frameworks) {
		this.frameworks = frameworks;
	}

	/**
	 * Creates and outputs HTML diagrams.
	 * 
	 * @param benchmarks
	 *            Results of logging framework benchmarks
	 */
	public void output(final Map<BenchmarkEntity, Map<String, List<BigDecimal>>> benchmarks) {
		for (Entry<BenchmarkEntity, Map<String, List<BigDecimal>>> benchmark : benchmarks.entrySet()) {
			String benchmarkName = benchmark.getKey().getBenchmark();
			LocationInfo locationInfo = benchmark.getKey().getLocation();

			BigDecimal max = benchmarks.entrySet().stream()
				.filter(entry -> Objects.equals(benchmarkName, entry.getKey().getBenchmark()))
				.flatMap(entry -> entry.getValue().values().stream())
				.flatMap(Collection::stream)
				.max(Comparator.naturalOrder())
				.orElse(BigDecimal.ONE);

			System.out.println(benchmark.getKey());
			System.out.println();

			System.out.println("<div class=\"table-responsive\"><table class=\"table benchmark\">");

			System.out.println("\t<thead>");
			System.out.println("\t\t<tr>");

			if (locationInfo == null) {
				System.out.println("\t\t\t<th>No-Op Benchmark</th>");
				System.out.println("\t\t\t<th>Invocations per Second</th>");
			} else {
				System.out.println("\t\t\t<th>Framework</th>");
				System.out.println("\t\t\t<th>Processed Log Entries per Second</th>");
			}

			System.out.println("\t\t</tr>");
			System.out.println("\t</thead>");

			System.out.println("\t<tbody>");

			for (Entry<String, Framework> framework : frameworks.entrySet()) {
				List<BigDecimal> scores = benchmark.getValue().get(framework.getKey());
				if (scores != null) {
					for (int i = 0; i < scores.size(); ++i) {
						BigDecimal total = scores.get(i).setScale(0, RoundingMode.HALF_UP);
						BigDecimal percentage = scores.get(i).multiply(PERCENTAGE).divide(max, DECIMAL_PLACES, RoundingMode.HALF_UP);

						String label = i == 0 ? framework.getValue().getName() : framework.getValue().getAsync();

						System.out.println("\t\t" + (i == 0 ? "<tr>" : "<tr class=\"advanced\">"));
						System.out.println("\t\t\t<td>" + label + "</td>");
						System.out.println("\t\t\t<td>");
						System.out.println("\t\t\t\t<div class=\"bar\" style=\"width: " + percentage + "%\">&nbsp;</div>");
						System.out.println("\t\t\t\t<div class=\"total\">" + NUMBER_FORMAT.format(total) + "</div></td>");
						System.out.println("\t\t\t</td>");
						System.out.println("\t\t</tr>");
					}
				}
			}

			System.out.println("\t</tbody>");

			System.out.println("</table></div>");
			System.out.println();
		}
	}

}
