package org.tinylog.impl.format.json;

import java.util.LinkedHashMap;
import java.util.Map;

import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.impl.format.FormatPatternParser;
import org.tinylog.impl.format.OutputFormat;
import org.tinylog.impl.format.OutputFormatBuilder;
import org.tinylog.impl.format.placeholder.Placeholder;

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
    public OutputFormat create(TinylogContext context) {
        FormatPatternParser parser = new FormatPatternParser(context);
        Configuration subConfiguration = context.getConfiguration().getSubConfiguration("fields");
        Map<String, Placeholder> fields = new LinkedHashMap<>();

        for (String key : subConfiguration.getKeys()) {
            fields.put(key, parser.parse(subConfiguration.getValue(key)));
        }

        if (fields.isEmpty()) {
            context.getLogger().log(Level.WARN, "No fields defined for newline-delimited JSON");
        }

        return new NewlineDelimitedJson(fields);
    }

}
