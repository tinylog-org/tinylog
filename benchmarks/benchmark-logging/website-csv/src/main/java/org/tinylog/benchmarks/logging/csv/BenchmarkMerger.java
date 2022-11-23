package org.tinylog.benchmarks.logging.csv;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.tinylog.benchmarks.logging.core.internal.ProjectLocation;

import fuzzycsv.FuzzyCSVTable;

/**
 * Merger for merging logging benchmark results into a single CSV file.
 */
public class BenchmarkMerger {

    private final Path parent;
    private final String[] folders;

    /**
     * @param parent The parent project folder that contains the logging benchmark projects
     * @param folders All folders with logging benchmark projects
     */
    public BenchmarkMerger(Path parent, String[] folders) {
        this.parent = parent;
        this.folders = folders;
    }

    /**
     * Merges all logging benchmark results into a single CSV file.
     *
     * @param targetFile The path to the target CSV file
     * @throws IOException Failed to read from a source file or to write to the target file
     */
    public void merge(Path targetFile) throws IOException {
        List<FuzzyCSVTable> benchmarks = loadBenchmarks(parent);
        FuzzyCSVTable mergedBenchmark = mergeBenchmarks(benchmarks);
        writeFile(targetFile, mergedBenchmark.toCsvString());
    }

    /**
     * Loads all benchmark results for the logging benchmark projects in the passed base path.
     *
     * @param parent The parent project folder that contains the logging benchmark projects
     * @return All logging benchmark results
     * @throws IOException Failed to read a benchmark name file or benchmark CSV file
     */
    private List<FuzzyCSVTable> loadBenchmarks(Path parent) throws IOException {
        List<FuzzyCSVTable> benchmarks = new ArrayList<>();

        for (String folder : folders) {
            Path projectDirectory = parent.resolve(folder);
            String name = readFile(projectDirectory.resolve(ProjectLocation.BENCHMARK_NAME_FILE));
            String csv = readFile(projectDirectory.resolve(ProjectLocation.BENCHMARK_CSV_FILE));
            benchmarks.add(resolveBenchmark(name, csv));
        }

        return benchmarks;
    }

    /**
     * Merges all passed benchmark results into a single CSV table.
     *
     * @param benchmarks All logging benchmark results to merge
     * @return A single CSV table that contains all data of the passed benchmark results
     * @throws NoSuchElementException The passed list of benchmark results was empty
     */
    private FuzzyCSVTable mergeBenchmarks(List<FuzzyCSVTable> benchmarks) {
        return benchmarks.stream()
            .reduce(FuzzyCSVTable::union)
            .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Converts the passed CSV string into a table and replaces the benchmark classes with the passed human-readable
     * name of the benchmarked logging framework.
     *
     * @param name The human-readable name of the benchmarked logging framework
     * @param csv The benchmark results in CSV format
     * @return The converted CSV table
     */
    private static FuzzyCSVTable resolveBenchmark(String name, String csv) {
        FuzzyCSVTable data = FuzzyCSVTable.fromCsvString(csv);

        for (int i = 0; i < data.size(); ++i) {
            String original = (String) data.row(i + 1).getAt(0);
            String transformed = original.replaceAll("(.+)\\.", name.trim() + " / ");
            data.putInCell(0, i + 1, transformed);
        }

        return data;
    }

    /**
     * Reads a text file.
     *
     * @param path The path to the text file
     * @return The content of the passed file
     * @throws IOException Failed to read the passed file
     */
    private static String readFile(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Writes a text file.
     *
     * <p>
     *     If the file already exists, it will be overwritten.
     * </p>
     *
     * @param path The path to the text file
     * @param content The text content for the passed file
     * @throws IOException Failed to write to the passed file
     */
    private static void writeFile(Path path, String content) throws IOException {
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        Files.write(path, data);
    }

}
