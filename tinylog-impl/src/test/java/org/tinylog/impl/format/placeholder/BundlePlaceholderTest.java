package org.tinylog.impl.format.placeholder;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;
import org.tinylog.test.util.FormatOutputRenderer;
import org.tinylog.test.util.LogEntryBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BundlePlaceholderTest {

    /**
     * Verifies that the bundle placeholder returns the output details of the child placeholder with the highest
     * requirements.
     */
    @Test
    void provideOutputDetails() {
        Placeholder firstChild = mock(Placeholder.class);
        when(firstChild.getOutputDetails()).thenReturn(OutputDetails.ENABLED_WITHOUT_LOCATION_INFO);

        Placeholder secondChild = mock(Placeholder.class);
        when(secondChild.getOutputDetails()).thenReturn(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);

        Placeholder thirdChild = mock(Placeholder.class);
        when(thirdChild.getOutputDetails()).thenReturn(OutputDetails.DISABLED);

        BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(List.of(firstChild, secondChild, thirdChild));
        assertThat(bundlePlaceholder.getOutputDetails()).isEqualTo(OutputDetails.ENABLED_WITH_CALLER_CLASS_NAME);
    }

    /**
     * Verifies that all child placeholders are resolved as a combined char sequence in the expected order.
     */
    @Test
    void resolveString() {
        StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
        ClassPlaceholder secondChild = new ClassPlaceholder();
        BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

        LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
        assertThat(bundlePlaceholder.getType()).isEqualTo(ValueType.STRING);
        assertThat(bundlePlaceholder.getValue(logEntry)).isEqualTo("Class: foo.MyClass");
    }

    /**
     * Verifies that all child placeholders are rendered correctly and in the expected order.
     */
    @Test
    void renderString() {
        StaticTextPlaceholder firstChild = new StaticTextPlaceholder("Class: ");
        ClassPlaceholder secondChild = new ClassPlaceholder();
        BundlePlaceholder bundlePlaceholder = new BundlePlaceholder(Arrays.asList(firstChild, secondChild));

        FormatOutputRenderer renderer = new FormatOutputRenderer(bundlePlaceholder);
        LogEntry logEntry = new LogEntryBuilder().className("foo.MyClass").create();
        assertThat(renderer.render(logEntry)).isEqualTo("Class: foo.MyClass");
    }

}
