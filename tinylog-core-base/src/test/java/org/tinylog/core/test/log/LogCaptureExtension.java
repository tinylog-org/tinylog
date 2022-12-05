package org.tinylog.core.test.log;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.tinylog.core.ConfigurationBuilder;
import org.tinylog.core.Framework;
import org.tinylog.core.Level;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.test.AbstractParameterizedExtension;

/**
 * JUnit extension for capturing output log entries.
 *
 * <p>
 *     Use the annotation {@link CaptureLogEntries} to apply this extension.
 * </p>
 */
public class LogCaptureExtension extends AbstractParameterizedExtension {

    /** */
    public LogCaptureExtension() {
        registerParameter(Framework.class, this::getOrCreateFramework);
        registerParameter(Log.class, this::getOrCreateLog);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws IllegalAccessException {
        List<CaptureLogEntries> annotations = findAnnotations(context, CaptureLogEntries.class);
        CaptureLogEntries lastAnnotation = annotations.isEmpty() ? null : annotations.get(annotations.size() - 1);

        Level level = lastAnnotation == null ? Level.WARN : lastAnnotation.level();
        put(context, Level.class, level);

        Framework framework = getOrCreateFramework(context);
        injectFields(context, framework);

        TestClock clock = getOrCreateClock(context);
        injectFields(context, clock);

        Log log = getOrCreateLog(context);
        injectFields(context, log);

        ConfigurationBuilder configuration = framework.getConfigurationBuilder(true);
        annotations.stream().flatMap(annotation -> Arrays.stream(annotation.configuration())).forEach(entry -> {
            int index = entry.indexOf('=');
            if (index >= 0) {
                String key = entry.substring(0, index).trim();
                String value = entry.substring(index + 1).trim();
                configuration.set(key, value);
            }
        });
        configuration.activate();

        if (lastAnnotation == null || lastAnnotation.autostart()) {
            log.setLevel(Level.mostSevereLevel(level, Level.WARN));
            framework.startUp();
        }

        log.setLevel(level);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            Level level = get(context, Level.class);
            Framework framework = get(context, Framework.class);
            Log log = get(context, Log.class);

            log.setLevel(Level.mostSevereLevel(level, Level.WARN));
            framework.shutDown();
            log.setLevel(level);

            Assertions
                .assertThat(log.consume())
                .as("Log should be empty after JUnit test")
                .isEmpty();
        } finally {
            remove(context, Level.class);
            remove(context, TestClock.class);
            remove(context, Framework.class);
            remove(context, CaptureLoggingBackend.class);
            remove(context, Log.class);
        }
    }

    /**
     * Gets the actual {@link Framework} instance from the store. If there is no {@link Framework} present in the store,
     * a new {@link Framework} will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link Framework} instance from the store
     */
    private Framework getOrCreateFramework(ExtensionContext context) {
        return getOrCreate(
            context,
            Framework.class,
            () -> new Framework(false, false) {
                @Override
                public Clock getClock() {
                    return getOrCreateClock(context);
                }

                @Override
                protected LoggingBackend createLoggingBackend() {
                    return getOrCreateLoggingBackend(context);
                }
            }
        );
    }

    /**
     * Gets the actual {@link TestClock} instance from the store. If there is no {@link TestClock} present in the store,
     * a new {@link TestClock} will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link TestClock} instance from the store
     */
    private TestClock getOrCreateClock(ExtensionContext context) {
        return getOrCreate(
            context,
            TestClock.class,
            () -> new TestClock(Clock.systemDefaultZone())
        );
    }

    /**
     * Gets the actual {@link CaptureLoggingBackend} instance from the store. If there is no
     * {@link CaptureLoggingBackend} present in the store, a new {@link CaptureLoggingBackend} will be created and added
     * to the store.
     *
     * @param context The current extension context
     * @return The {@link CaptureLoggingBackend} instance from the store
     */
    private CaptureLoggingBackend getOrCreateLoggingBackend(ExtensionContext context) {
        return getOrCreate(
            context,
            CaptureLoggingBackend.class,
            () -> new CaptureLoggingBackend(
                getOrCreateFramework(context),
                getOrCreateLog(context),
                get(context, Level.class)
            )
        );
    }

    /**
     * Gets the actual {@link Log} instance from the store. If there is no {@link Log} present in the store, a new
     * {@link Log} will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link Log} instance from the store
     */
    private Log getOrCreateLog(ExtensionContext context) {
        return getOrCreate(context, Log.class, Log::new);
    }

}
