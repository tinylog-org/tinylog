package org.tinylog.core.test.system;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.assertj.core.api.Assertions;
import org.assertj.core.util.Strings;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.tinylog.core.test.AbstractParameterizedExtension;

/**
 * JUnit extension for capturing output from {@link System#out} and {@link System#err}.
 *
 * <p>
 *     Use the annotation {@link CaptureSystemOutput} to apply this extension.
 * </p>
 */
public class SystemStreamCaptureExtension extends AbstractParameterizedExtension {

    private static final String SYSTEM_OUT_KEY = "out";
    private static final String SYSTEM_ERR_KEY = "err";

    /** */
    public SystemStreamCaptureExtension() {
        registerParameter(Output.class, this::getOrCreateOutput);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws IllegalAccessException, UnsupportedEncodingException {
        Output output = getOrCreateOutput(context);
        injectFields(context, output);

        PrintStream out = getStore(context).get(SYSTEM_OUT_KEY, PrintStream.class);
        if (out == null) {
            getStore(context).put(SYSTEM_OUT_KEY, System.out);
        }

        PrintStream err = getStore(context).get(SYSTEM_ERR_KEY, PrintStream.class);
        if (err == null) {
            getStore(context).put(SYSTEM_ERR_KEY, System.err);
        }

        List<CaptureSystemOutput> annotations = findAnnotations(context, CaptureSystemOutput.class);

        String[] excludes;
        if (annotations.size() == 0) {
            excludes = new String[0];
        } else {
            excludes = annotations.get(annotations.size() - 1).excludes();
        }

        Pattern[] patterns = Arrays.stream(excludes).map(Pattern::compile).toArray(Pattern[]::new);
        output.setExcludes(patterns);

        OutputStream outputStream = getOrCreateForwardStream(context);
        PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8.name());

        System.setOut(printStream);
        System.setErr(printStream);
    }

    @Override
    public void afterEach(ExtensionContext context) throws UnsupportedEncodingException {
        PrintStream out = getStore(context).get(SYSTEM_OUT_KEY, PrintStream.class);
        System.setOut(out);

        PrintStream err = getStore(context).get(SYSTEM_ERR_KEY, PrintStream.class);
        System.setErr(err);

        getOrCreateForwardStream(context).flush();

        Assertions
            .assertThat(getOrCreateOutput(context).consume())
            .as("Output should be empty after JUnit test")
            .isEmpty();
    }

    /**
     * Gets the actual {@link ForwardStream} instance from the store. If there is no {@link ForwardStream} present in
     * the store, a new {@link ForwardStream} will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link ForwardStream} instance from the store
     */
    private ForwardStream getOrCreateForwardStream(ExtensionContext context) {
        return getOrCreate(context, ForwardStream.class, () -> new ForwardStream(getOrCreateOutput(context)));
    }

    /**
     * Gets the actual {@link Output} instance from the store. If there is no {@link Output} present in the store, a new
     * {@link Output} will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link Output} instance from the store
     */
    private Output getOrCreateOutput(ExtensionContext context) {
        return getOrCreate(context, Output.class, Output::new);
    }

    /**
     * Wrapper stream that forwards complete lines to {@link Output}.
     */
    private static final class ForwardStream extends OutputStream {

        private static final String newLine = System.lineSeparator();

        private final Output output;
        private final ByteArrayOutputStream stream;

        /**
         * @param output All captured lines will be added to this {@link Output} instance
         */
        private ForwardStream(Output output) {
            this.output = output;
            this.stream = new ByteArrayOutputStream();
        }

        @Override
        public void write(int value) throws UnsupportedEncodingException {
            stream.write(value);

            String line = stream.toString(StandardCharsets.UTF_8.name());
            if (line.endsWith(newLine)) {
                output.add(line.substring(0, line.length() - newLine.length()));
                stream.reset();
            }
        }

        /**
         * Flushes all remaining characters as last line.
         *
         * @throws UnsupportedEncodingException UTF-8 charset is not supported
         */
        public void flush() throws UnsupportedEncodingException {
            String line = stream.toString(StandardCharsets.UTF_8.name());
            if (!Strings.isNullOrEmpty(line)) {
                output.add(line);
            }

            stream.reset();
        }

    }

}
