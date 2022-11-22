package org.tinylog.benchmarks.logging.core.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;

import org.tinylog.benchmarks.logging.core.BenchmarkInfo;

/**
 * Utility class for resolving the human-readable name of the benchmarked logging framework and saving it to a file.
 */
class BenchmarkName {

    private final ServiceLoader<BenchmarkInfo> serviceLoader;

    /**
     * @param serviceLoader The service loader to use for resolving the service implementation with the meta information
     *                      for the benchmarked logging framework
     */
    BenchmarkName(ServiceLoader<BenchmarkInfo> serviceLoader) {
        this.serviceLoader = serviceLoader;
    }

    /**
     * Resolves the human-readable of the logging framework via the service interface {@link BenchmarkInfo} and stores
     * the first found name in the file defined in {@link ProjectLocation#BENCHMARK_NAME_FILE}.
     *
     * <p>
     *     The version number will be included in the name of logging framework if the implementation version of the
     *     logging framework is available.
     * </p>
     *
     * <p>
     *     If there is no service implementation for {@link BenchmarkInfo} in the class path or the service
     *     implementation does not provide a logger class, no file will be created.
     * </p>
     *
     * @throws URISyntaxException Failed to convert the location URL of the logger class into a valid URI
     * @throws IOException Failed to write the name of logging framework into the target file
     */
    void store() throws URISyntaxException, IOException {
        Optional<BenchmarkInfo> optionalBenchmark = serviceLoader.findFirst();

        if (optionalBenchmark.isPresent()) {
            BenchmarkInfo benchmark = optionalBenchmark.get();

            String name = benchmark.getName();
            Class<?> logger = benchmark.getLogger();
            String version = logger == null ? null : logger.getPackage().getImplementationVersion();
            String fullName = version == null ? name : name + " " + version;

            Path path = new ProjectLocation(benchmark.getClass()).resolve(ProjectLocation.BENCHMARK_NAME_FILE);
            Files.write(path, fullName.getBytes(StandardCharsets.UTF_8));
        }
    }

}
