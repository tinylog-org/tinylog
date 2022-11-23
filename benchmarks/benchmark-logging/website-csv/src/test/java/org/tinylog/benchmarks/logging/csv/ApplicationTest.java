package org.tinylog.benchmarks.logging.csv;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockConstruction;

class ApplicationTest {

    /**
     * Verifies that the application can successfully execute {@link BenchmarkMerger#merge(Path)}.
     */
    @Test
    void execution() throws Exception {
        try (MockedConstruction<BenchmarkMerger> mocked = mockConstruction(BenchmarkMerger.class)) {
            Application.main(new String[0]);
            assertThat(mocked.constructed()).hasSize(1);
        }
    }

}
