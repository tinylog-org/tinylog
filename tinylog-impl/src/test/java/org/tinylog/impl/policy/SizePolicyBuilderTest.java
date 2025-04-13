package org.tinylog.impl.policy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ServiceLoader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@Tinylog
class SizePolicyBuilderTest {

    @Inject
    private TinylogContext context;

    private Path logFile;

    /**
     * Verifies that the builder is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(PolicyBuilder.class)).anySatisfy(builder -> {
            assertThat(builder).isInstanceOf(SizePolicyBuilder.class);
            assertThat(builder.getName()).isEqualTo("size");
        });
    }

    /**
     * Creates a temporary log file.
     */
    @BeforeEach
    void init() throws IOException {
        logFile = Files.createTempFile("tinylog", ".log");
        logFile.toFile().deleteOnExit();
    }

    /**
     * Deletes the created temporary log file.
     */
    @AfterEach
    void release() throws IOException {
        Files.deleteIfExists(logFile);
    }

    /**
     * Verifies that the passed configuration values can be passed and the created size policy accepts log entries until
     * the expected maximum file size.
     *
     * @param configurationValue The configuration value with the maximum file size
     * @param maxFileSize The maximum file size in bytes
     */
    @ParameterizedTest
    @CsvSource({
        " 512,         512",
        "1 kb,        1024",
        "2 KB,        2048",
        "10KB,       10240",
        "1 mb,     1048576",
        "2 MB,     2097152",
        "10MB,    10485760",
        "1 gb,  1073741824",
        "2 GB,  2147483648",
        "10GB, 10737418240"
    })
    void validFileSize(String configurationValue, long maxFileSize) throws Exception {
        Policy policy = new SizePolicyBuilder().create(context, configurationValue);
        policy.init(logFile);

        for (int i = 0; i < maxFileSize / Integer.MAX_VALUE; ++i) {
            assertThat(policy.canAcceptDataRecord(Integer.MAX_VALUE)).isTrue();
        }

        assertThat(policy.canAcceptDataRecord((int) (maxFileSize % Integer.MAX_VALUE))).isTrue();
        assertThat(policy.canAcceptDataRecord(1)).isFalse();
    }

    /**
     * Verifies that an exception with a meaningful message will be thrown, if the configuration value for the maximum
     * file size is no set.
     *
     * @param configurationValue Missing configuration value for the maximum file size
     */
    @ParameterizedTest
    @NullAndEmptySource
    void missingFileSize(String configurationValue) {
        Throwable throwable = catchThrowable(() -> new SizePolicyBuilder().create(context, configurationValue));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("maximum file size");
    }

    /**
     * Verifies that an exception with a meaningful message will be thrown, if the configuration value for the maximum
     * file size contains an invalid value.
     *
     * @param configurationValue The configuration value with an invalid value for the maximum file size
     */
    @ParameterizedTest
    @ValueSource(strings = {"foo", "MB"})
    void invalidFileSize(String configurationValue) {
        Throwable throwable = catchThrowable(() -> new SizePolicyBuilder().create(context, configurationValue));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining(configurationValue);
    }

}
