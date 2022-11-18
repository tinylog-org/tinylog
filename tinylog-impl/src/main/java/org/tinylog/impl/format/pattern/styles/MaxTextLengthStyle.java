package org.tinylog.impl.format.pattern.styles;

import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Styled placeholder wrapper for applying a configurable maximum length.
 */
public class MaxTextLengthStyle extends AbstractMaxLengthStyle {

    /**
     * @param placeholder The actual placeholder to style
     * @param maxLength The maximum length for the input string
     */
    public MaxTextLengthStyle(Placeholder placeholder, int maxLength) {
        super(placeholder, maxLength);
    }

    @Override
    protected void apply(StringBuilder builder, int start) {
        int totalLength = builder.length();
        int valueLength = totalLength - start;
        int difference = valueLength - getMaxLength();

        if (difference > 0) {
            if (getMaxLength() >= ELLIPSIS.length()) {
                builder.setLength(totalLength - difference - ELLIPSIS.length());
                builder.append(ELLIPSIS);
            } else {
                builder.setLength(totalLength - difference);
            }
        }
    }

}
