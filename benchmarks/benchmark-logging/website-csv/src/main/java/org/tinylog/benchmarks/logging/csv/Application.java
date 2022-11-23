package org.tinylog.benchmarks.logging.csv;

import java.nio.file.Path;

import org.tinylog.Logger;
import org.tinylog.benchmarks.logging.core.internal.ProjectLocation;

/**
 * The main application class for merging all logging benchmark results.
 */
public final class Application {

    private static final String[] BENCHMARK_FOLDERS = {
        "benchmark-tinylog3",
        "benchmark-tinylog2",
        "benchmark-log4j2",
        "benchmark-logback",
        "benchmark-jul",
        "benchmark-noop",
    };

    private static final String TARGET_CSV_FILE = "src/main/resources/benchmark.csv";

    /** */
    private Application() {
    }

    /**
     * Executes the {@link BenchmarkMerger}.
     *
     * @param args Arguments are not supported and will be ignored
     * @throws Exception Failed to merge CSV files
     */
    public static void main(String[] args) throws Exception {
        ProjectLocation projectLocation = new ProjectLocation(Application.class);
        Path targetPath = projectLocation.resolve(TARGET_CSV_FILE);

        Logger.info("Project directory: {}", projectLocation.getBasePath());
        Logger.info("Total logging benchmarks: {}", BENCHMARK_FOLDERS.length);

        new BenchmarkMerger(projectLocation.getParentPath(), BENCHMARK_FOLDERS).merge(targetPath);

        Logger.info("Merged benchmark CSV file: {}", targetPath);
    }

}
