package org.tinylog.benchmarks.logging.core.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;

/**
 * Location of a local Maven project.
 */
public final class ProjectLocation {

    /**
     * File name for Maven's POM XML.
     */
    public static final String POM_XML = "pom.xml";

    /**
     * Path to the text file that should contain the human-readable name of the benchmarked logging framework.
     */
    public static final String BENCHMARK_NAME_FILE = "target/benchmark-name.txt";

    /**
     * Path to the CSV file that should contain the benchmark results of the benchmarked logging framework.
     */
    public static final String BENCHMARK_CSV_FILE = "target/benchmark.csv";

    private final Path basePath;

    /**
     * @param clazz Any class of a local Maven project
     * @throws IllegalArgumentException Failed to resolve the code source location or to find the {@code pom.xml}
     * @throws URISyntaxException Failed to convert the location URL into a valid URI
     */
    public ProjectLocation(Class<?> clazz) throws URISyntaxException {
        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalArgumentException("Cannot resolve code source location for  \"" + clazz + "\"");
        } else {
            URI location = source.getLocation().toURI();
            basePath = resolveBasePath(location);
        }
    }

    /**
     * Gets the file system path of the local Maven project.
     *
     * <p>
     *     This is the folder that contains the {@code pom.xml} of the Maven project.
     * </p>
     *
     * @return The path to the project folder
     */
    public Path getBasePath() {
        return basePath;
    }

    /**
     * Gets the parent folder.
     *
     * <p>
     *     This is usually the folder that contains the parent POM.
     * </p>
     *
     * @return The path to the parent folder
     */
    public Path getParentPath() {
        return basePath.getParent();
    }

    /**
     * Resolves a file relative to the project folder.
     *
     * @param file The file to resolve (sub folders are supported within the passed path)
     * @return The full path to the passed file
     */
    public Path resolve(String file) {
        return basePath.resolve(file);
    }

    /**
     * Resolves the file system path of the local Maven project.
     *
     * @param location The URI of any file or folder within the local Maven project
     * @return The path to the project folder
     * @throws IllegalArgumentException Failed to find the {@code pom.xml}
     */
    private static Path resolveBasePath(URI location) {
        Path path = Paths.get(location);

        for (Path folder = path; folder != null; folder = folder.getParent()) {
            Path file = folder.resolve(POM_XML);
            if (Files.isRegularFile(file)) {
                return folder;
            }
        }

        throw new IllegalArgumentException("Cannot find \"" + POM_XML + "\" in \"" + path + "\" nor in parent folders");
    }

}
