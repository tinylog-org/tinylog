package org.tinylog.core.test.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.tinylog.core.Level;

/**
 * JUnit extension annotation for capturing output log entries.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtendWith(LogCaptureExtension.class)
public @interface CaptureLogEntries {

    /**
     * Key value pairs to apply to the framework's configuration.
     *
     * <p>
     * Examples:
     * <blockquote><pre>
     * {@literal @}CaptureLogEntries(configuration = "locale=en_US")
     * {@literal @}CaptureLogEntries(configuration = {"backends=nop", "locale=en_US"})
     * </pre></blockquote>
     * </p>
     *
     * @return The initial configuration to apply to the framework
     */
    String[] configuration() default "";

    /**
     * All log entries with a severity level less severe than the configured level are ignored.
     *
     * @return The configured severity level
     */
    Level level() default Level.WARN;

    /**
     * By default ({@code autostart = true}, the extension will automatically start the provided framework. However,
     * autostart can be disabled by setting {@code autostart = false}.
     *
     * @return {@code true} if the framework should be automatically started, {@code false} if the framework should not
     *         be started in advance
     */
    boolean autostart() default true;

}
