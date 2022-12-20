package org.tinylog.core.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.tinylog.core.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class LevelVisibilityTest {

    /**
     * Verifies that output details for {@link Level#TRACE} can be received.
     *
     * @param outputDetails The output details for {@link Level#TRACE}
     */
    @ParameterizedTest
    @EnumSource(OutputDetails.class)
    void trace(OutputDetails outputDetails) {
        LevelVisibility visibility = new LevelVisibility(
            outputDetails,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED
        );

        assertThat(visibility.getTrace()).isEqualTo(outputDetails);
        assertThat(visibility.get(Level.TRACE)).isEqualTo(outputDetails);
    }

    /**
     * Verifies that output details for {@link Level#DEBUG} can be received.
     *
     * @param outputDetails The output details for {@link Level#DEBUG}
     */
    @ParameterizedTest
    @EnumSource(OutputDetails.class)
    void debug(OutputDetails outputDetails) {
        LevelVisibility visibility = new LevelVisibility(
            OutputDetails.DISABLED,
            outputDetails,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED
        );

        assertThat(visibility.getDebug()).isEqualTo(outputDetails);
        assertThat(visibility.get(Level.DEBUG)).isEqualTo(outputDetails);
    }

    /**
     * Verifies that output details for {@link Level#INFO} can be received.
     *
     * @param outputDetails The output details for {@link Level#INFO}
     */
    @ParameterizedTest
    @EnumSource(OutputDetails.class)
    void info(OutputDetails outputDetails) {
        LevelVisibility visibility = new LevelVisibility(
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            outputDetails,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED
        );

        assertThat(visibility.getInfo()).isEqualTo(outputDetails);
        assertThat(visibility.get(Level.INFO)).isEqualTo(outputDetails);
    }

    /**
     * Verifies that output details for {@link Level#WARN} can be received.
     *
     * @param outputDetails The output details for {@link Level#WARN}
     */
    @ParameterizedTest
    @EnumSource(OutputDetails.class)
    void warn(OutputDetails outputDetails) {
        LevelVisibility visibility = new LevelVisibility(
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            outputDetails,
            OutputDetails.DISABLED
        );

        assertThat(visibility.getWarn()).isEqualTo(outputDetails);
        assertThat(visibility.get(Level.WARN)).isEqualTo(outputDetails);
    }

    /**
     * Verifies that output details for {@link Level#ERROR} can be received.
     *
     * @param outputDetails The output details for {@link Level#ERROR}
     */
    @ParameterizedTest
    @EnumSource(OutputDetails.class)
    void error(OutputDetails outputDetails) {
        LevelVisibility visibility = new LevelVisibility(
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            OutputDetails.DISABLED,
            outputDetails
        );

        assertThat(visibility.getError()).isEqualTo(outputDetails);
        assertThat(visibility.get(Level.ERROR)).isEqualTo(outputDetails);
    }

    /**
     * Verifies that no output details are available for {@link Level#OFF}.
     */
    @Test
    void off() {
        LevelVisibility visibility = new LevelVisibility(
            OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
            OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
            OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
            OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION,
            OutputDetails.ENABLED_WITHOUT_LOCATION_INFORMATION
        );

        assertThatCode(() -> visibility.get(Level.OFF))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("OFF");
    }

}
