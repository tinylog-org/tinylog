package org.tinylog.test.junit.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;
import org.tinylog.core.Level;

/**
 * Annotation for the JUnit extension {@link TinylogExtension}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ExtendWith(TinylogExtension.class)
public @interface Tinylog {

    /**
     * Key value pairs of the tinylog configuration to apply.
     *
     * <p>
     * Examples:
     * <blockquote><pre>
     * {@literal @}Tinylog(configuration = "locale=en_US")
     * {@literal @}Tinylog(configuration = {"backends=nop", "locale=en_US"})
     * </pre></blockquote>
     * </p>
     *
     * @return The initial tinylog configuration to apply
     */
    String[] configuration() default {};

    /**
     * All log entries with a severity level less severe than the configured level will be ignored.
     *
     * @return The minimum severity level
     */
    Level level() default Level.WARN;

}
