package org.tinylog.test.junit.log;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.TaskExecutor;
import org.tinylog.core.backend.LoggingBackend;
import org.tinylog.core.backend.TinylogContext;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.core.runtime.RuntimeBuilder;
import org.tinylog.core.runtime.RuntimeFlavor;
import org.tinylog.test.junit.AbstractParameterizedExtension;
import org.tinylog.test.util.SynchronousTaskExecutor;

/**
 * JUnit extension for applying a tinylog configuration and capturing log entries.
 *
 * <p>
 *     Use the annotation {@link Tinylog} to apply this extension.
 * </p>
 */
public class TinylogExtension extends AbstractParameterizedExtension implements BeforeEachCallback, AfterEachCallback {

    /** */
    public TinylogExtension() {
        registerParameter(TinylogContext.class, this::getTinylogContext);
        registerParameter(RuntimeFlavor.class, this::getRuntimeFlavor);
        registerParameter(ClassLoader.class, this::getClassLoader);
        registerParameter(TestClock.class, this::getTestClock);
        registerParameter(Configuration.class, this::getConfiguration);
        registerParameter(InternalLogger.class, this::getInternalLogger);
        registerParameter(Log.class, this::getLog);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws IllegalAccessException {
        injectFields(context, getTinylogContext(context));
        injectFields(context, getRuntimeFlavor(context));
        injectFields(context, getClassLoader(context));
        injectFields(context, getTestClock(context));
        injectFields(context, getConfiguration(context));
        injectFields(context, getInternalLogger(context));
        injectFields(context, getLog(context));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Configuration configuration = getConfiguration(context);
        Log log = getLog(context);

        Assertions
            .assertThat(log.consume())
            .map(entry -> entry.getSeverityLevel() + ": " + entry.getFormattedMessage(configuration))
            .as("Log should be empty after JUnit test")
            .isEmpty();
    }

    /**
     * Gets the actual {@link TinylogContext} instance from the store. If there is no {@link TinylogContext} instance
     * present in the store, a new {@link TinylogContext} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link TinylogContext} instance from the store
     */
    private TinylogContext getTinylogContext(ExtensionContext context) {
        return getOrCreate(context, TinylogContext.class, () -> new TinylogContext(
            getClassLoader(context),
            getTestClock(context),
            getRuntimeFlavor(context),
            getConfiguration(context),
            getInternalLogger(context)
        ));
    }

    /**
     * Gets the actual {@link ClassLoader} instance from the store. If there is no {@link ClassLoader} instance
     * present in the store, a new {@link ClassLoader} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link ClassLoader} instance from the store
     */
    private ClassLoader getClassLoader(ExtensionContext context) {
        return getOrCreate(context, ClassLoader.class, () -> Thread.currentThread().getContextClassLoader());
    }

    /**
     * Gets the actual {@link TestClock} instance from the store. If there is no {@link TestClock} instance
     * present in the store, a new {@link TestClock} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link TestClock} instance from the store
     */
    private TestClock getTestClock(ExtensionContext context) {
        return getOrCreate(context, TestClock.class, TestClock::new);
    }

    /**
     * Gets the actual {@link RuntimeFlavor} instance from the store. If there is no {@link RuntimeFlavor} instance
     * present in the store, a new {@link RuntimeFlavor} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link RuntimeFlavor} instance from the store
     */
    private RuntimeFlavor getRuntimeFlavor(ExtensionContext context) {
        return getOrCreate(context, RuntimeFlavor.class, () -> {
            for (RuntimeBuilder builder : ServiceLoader.load(RuntimeBuilder.class)) {
                if (builder.isSupported()) {
                    InternalLogger logger = getInternalLogger(context);
                    return builder.create(logger);
                }
            }

            throw new IllegalStateException("No supported runtime available");
        });
    }

    /**
     * Gets the actual {@link Configuration} instance from the store. If there is no {@link Configuration} instance
     * present in the store, a new {@link Configuration} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link Configuration} instance from the store
     */
    private Configuration getConfiguration(ExtensionContext context) {
        return getOrCreate(context, Configuration.class, () -> {
            InternalLogger logger = getInternalLogger(context);
            Map<String, String> properties = getConfigurationProperties(context);
            return new Configuration(properties, logger);
        });
    }

    /**
     * Gets the actual {@link InternalLogger} instance from the store. If there is no {@link InternalLogger} instance
     * present in the store, a new {@link InternalLogger} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link InternalLogger} instance from the store
     */
    private InternalLogger getInternalLogger(ExtensionContext context) {
        return getOrCreate(context, InternalLogger.class, () -> {
            TaskExecutor executor = getTaskExecutor(context);
            return new InternalLogger(executor);
        });
    }

    /**
     * Gets the actual {@link Log} instance from the store. If there is no {@link Log} instance present in the store,
     * a new {@link Log} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link Log} instance from the store
     */
    private Log getLog(ExtensionContext context) {
        return getOrCreate(context, Log.class, () -> {
            Level level = getServerityLevel(context);
            return new Log(level);
        });
    }

    /**
     * Gets the actual {@link TaskExecutor} instance from the store. If there is no {@link TaskExecutor} instance
     * present in the store, a new {@link TaskExecutor} instance will be created and added to the store.
     *
     * @param context The current extension context
     * @return The {@link TaskExecutor} instance from the store
     */
    private TaskExecutor getTaskExecutor(ExtensionContext context) {
        return getOrCreate(context, TaskExecutor.class, () -> {
            Log log = getLog(context);
            LoggingBackend backend = new PassThroughLoggingBackend(log);
            return new SynchronousTaskExecutor(backend);
        });
    }

    /**
     * Gets the minimum severity level.
     *
     * <p>
     *     Log entries that are less severe than the configured minimum severity level are discarded and ignored. If no
     *     minimum severity level is configured, {@link Level#WARN} will be used as default.
     * </p>
     *
     * @param context The current extension context
     * @return The minimum severity level
     */
    private Level getServerityLevel(ExtensionContext context) {
        List<Tinylog> annotations = findAnnotations(context, Tinylog.class);
        Tinylog lastAnnotation = annotations.isEmpty() ? null : annotations.get(annotations.size() - 1);
        return lastAnnotation == null ? Level.WARN : lastAnnotation.level();
    }

    /**
     * Collects all configuration properties to use for configuring tinylog.
     *
     * @param context The current extension context
     * @return All configuration properties
     */
    private Map<String, String> getConfigurationProperties(ExtensionContext context) {
        Map<String, String> properties = new LinkedHashMap<>();
        List<Tinylog> annotations = findAnnotations(context, Tinylog.class);

        annotations.stream().flatMap(annotation -> Arrays.stream(annotation.configuration())).forEach(entry -> {
            int index = entry.indexOf('=');
            if (index >= 0) {
                String key = entry.substring(0, index);
                String value = entry.substring(index + 1);
                properties.put(key, value);
            }
        });

        return properties;
    }

}
