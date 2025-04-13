package org.tinylog.impl.format.placeholder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.tinylog.core.LogEntry;
import org.tinylog.core.backend.OutputDetails;

/**
 * Bundle of multiple child placeholders.
 *
 * <p>
 *     This bundle placeholder combines the render result of multiple child placeholders. All child placeholders are
 *     rendered in the order in which they are passed.
 * </p>
 */
public class BundlePlaceholder implements Placeholder {

    private final List<Placeholder> placeholders;

    /**
     * @param placeholders Child placeholders
     */
    public BundlePlaceholder(List<Placeholder> placeholders) {
        this.placeholders = new ArrayList<>(placeholders);
    }

    @Override
    public OutputDetails getOutputDetails() {
        return placeholders.stream()
            .map(Placeholder::getOutputDetails)
            .max(Comparator.comparing(Enum::ordinal))
            .orElse(OutputDetails.DISABLED);
    }

    @Override
    public ValueType getType() {
        return ValueType.STRING;
    }

    @Override
    public String getValue(LogEntry entry) {
        StringBuilder builder = new StringBuilder();
        render(builder, entry);
        return builder.toString();
    }

    @Override
    public void render(StringBuilder builder, LogEntry entry) {
        placeholders.forEach(placeholder -> placeholder.render(builder, entry));
    }

}
