package org.tinylog.core.internal;

import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.collect.ImmutableMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AbstractPatternParserTest {

    @Mock
    private BiConsumer<StringBuilder, String> consumer;

    /**
     * Verifies that plain text is output unchanged.
     */
    @Test
    void plainText() {
        assertThat(parse("Hello World!")).isEqualTo("Hello World!");
        verifyNoInteractions(consumer);
    }

    /**
     * Verifies that one single quote can be output.
     */
    @Test
    void singleQuote() {
        setPlaceholders(ImmutableMap.of("time", "twelve"));
        assertThat(parse("It is {time} o'clock.")).isEqualTo("It is twelve o'clock.");
    }

    /**
     * Verifies that two directly consecutive singe quotes are output as one single quote.
     */
    @Test
    void escapedQuote() {
        setPlaceholders(ImmutableMap.of("time", "twelve"));
        assertThat(parse("It is {time} o''clock.")).isEqualTo("It is twelve o'clock.");
    }

    /**
     * Verifies that a single placeholder can be replaced with its assigned value.
     */
    @Test
    void singlePlaceholder() {
        setPlaceholders(ImmutableMap.of("foo", "Alice"));
        assertThat(parse("Hello {foo}!")).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that an escaped placeholder is output unchanged.
     */
    @Test
    void escapedPlaceholder() {
        assertThat(parse("Hello '{foo}'!")).isEqualTo("Hello {foo}!");
        verifyNoInteractions(consumer);
    }

    /**
     * Verifies that multiple placeholders can be replaced with assigned values.
     */
    @Test
    void multiplePlaceholders() {
        setPlaceholders(ImmutableMap.of("foo", "Alice", "bar", "Bob"));
        assertThat(parse("Hello {foo} and {bar}!")).isEqualTo("Hello Alice and Bob!");
    }

    /**
     * Verifies that brackets can be nested.
     */
    @Test
    void nestedBrackets() {
        setPlaceholders(ImmutableMap.of("{foo}", "Alice"));
        assertThat(parse("Hello {{foo}}!")).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that quote characters inside of brackets are passed unchanged.
     */
    @Test
    void nestedQuotedBrackets() {
        setPlaceholders(ImmutableMap.of("'{}'", "Alice"));
        assertThat(parse("Hello {'{}'}!")).isEqualTo("Hello Alice!");
    }

    /**
     * Verifies that an oping curly bracket is output unmodified if there is no corresponding closing curly bracket.
     */
    @Test
    void missingClosingBracket() {
        assertThat(parse("Here is a mistake: <{foo>")).isEqualTo("Here is a mistake: <{foo>");
        verifyNoInteractions(consumer);
    }

    /**
     * Assigns placeholders with real values.
     *
     * @param mapping The mapping with placeholders as key and the corresponding replacements as value
     */
    private void setPlaceholders(Map<String, String> mapping) {
        mapping.forEach((placeholder, value) -> doAnswer(call -> {
            StringBuilder builder = call.getArgument(0);
            builder.append(value);
            return Void.TYPE;
        }).when(consumer).accept(notNull(), eq(placeholder)));
    }

    /**
     * Calls {@link AbstractPatternParser#parse(String, BiConsumer)} with the passed pattern and all assigned
     * placeholder values.
     *
     * @param pattern The pattern to parse
     * @return The resolved text
     */
    private String parse(String pattern) {
        return new AbstractPatternParser() { }
            .parse(pattern, consumer)
            .toString();
    }

}
