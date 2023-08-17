package org.tinylog.benchmarks.logging.csv;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import fuzzycsv.FuzzyCSVTable;

import static fuzzycsv.FuzzyCSVTable.from;
import static fuzzycsv.FuzzyCSVTable.fromCsvString;
import static org.assertj.core.api.Assertions.assertThat;

class BenchmarkMergerTest {

    @TempDir
    private Path folder;

    /**
     * Verifies that CSV files will be merged correctly.
     */
    @Test
    void merge() throws IOException {
        writeFile(folder.resolve("foo/target/benchmark-name.txt"), "Foo 1.0");
        writeFile(folder.resolve("foo/target/benchmark.csv"), "Name,Number\norg.MyClass.foo,1");
        writeFile(folder.resolve("bar/target/benchmark-name.txt"), "Bar 2.0-M1");
        writeFile(folder.resolve("bar/target/benchmark.csv"), "Name,Number\norg.MyClass.bar,2");

        Path target = folder.resolve("merged.csv");
        new BenchmarkMerger(folder, new String[] {"foo", "bar"}).merge(target);

        assertThat(target).isRegularFile().satisfies(file -> {
            try (Reader reader = Files.newBufferedReader(file)) {
                FuzzyCSVTable expected = fromCsvString("Name,Number\nFoo 1.0 / foo,1\nBar 2.0-M1 / bar,2");
                FuzzyCSVTable actual = from().csv().parse(reader);
                assertThat(expected).isEqualTo(actual);
            }
        });
    }

    /**
     * Writes a text path.
     *
     * <p>
     *     Non existing sub folders are created automatically.
     * </p>
     *
     * @param path The path to the text file
     * @param content The text content for the passed file
     * @throws IOException Failed to write to the passed file
     */
    private void writeFile(Path path, String content) throws IOException {
        Files.createDirectories(path.getParent());
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        Files.write(path, data);
    }

}
