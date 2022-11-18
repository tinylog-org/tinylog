package org.tinylog.impl.format.json;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tinylog.core.Configuration;
import org.tinylog.core.Framework;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.format.pattern.FormatPatternParser;
import org.tinylog.impl.format.pattern.placeholders.Placeholder;

/**
 * Builder for creating an instance of {@link NewlineDelimitedJson}.
 */
public class NewlineDelimitedJsonBuilder implements OutputFormatBuilder {

    /** */
    public NewlineDelimitedJsonBuilder() {
    }

    @Override
    public String getName() {
        return "ndjson";
    }

    @Override
    public OutputFormat create(Framework framework, Configuration configuration) {
        FormatPatternParser parser = new FormatPatternParser(framework);
        Configuration subConfiguration = configuration.getSubConfiguration("fields");
        Map<String, Placeholder> fields = new LinkedHashMap<>();

        for (String key : subConfiguration.getKeys()) {
            fields.put(key, parser.parse(subConfiguration.getValue(key)));
        }

        if (fields.isEmpty()) {
            InternalLogger.warn(null, "No fields defined for newline-delimited JSON");
        }

        return new NewlineDelimitedJson(fields);
    }

}
