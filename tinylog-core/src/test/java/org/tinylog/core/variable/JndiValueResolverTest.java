package org.tinylog.core.variable;

import java.util.ServiceLoader;

import javax.naming.InitialContext;
import javax.naming.InvalidNameException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.tinylog.core.Configuration;
import org.tinylog.core.Level;
import org.tinylog.core.internal.InternalLogger;
import org.tinylog.test.junit.log.Log;
import org.tinylog.test.junit.log.Tinylog;

import jakarta.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@Tinylog
class JndiValueResolverTest {

    @Inject
    private Configuration configuration;

    @Inject
    private InternalLogger logger;

    @Inject
    private Log log;

    /**
     * Verifies that the resolver is registered as service.
     */
    @Test
    void service() {
        assertThat(ServiceLoader.load(VariableResolver.class))
            .anyMatch(loader -> loader instanceof JndiValueResolver);
    }

    /**
     * Verifies that the name is "JNDI value".
     */
    @Test
    void name() {
        JndiValueResolver resolver = new JndiValueResolver();
        assertThat(resolver.getName()).isEqualTo("JNDI value");
    }

    /**
     * Verifies that the prefix character is "@".
     */
    @Test
    void prefix() {
        JndiValueResolver resolver = new JndiValueResolver();
        assertThat(resolver.getPrefix()).isEqualTo("@");
    }

    /**
     * Verifies that the String value of a JNDI name can be resolved.
     */
    @Test
    void resolveJndiStringValue() {
        try (MockedStatic<InitialContext> mock = mockStatic(InitialContext.class)) {
            mock.when(() -> InitialContext.doLookup("java:comp/env/foo")).thenReturn("bar");

            JndiValueResolver resolver = new JndiValueResolver();
            assertThat(resolver.resolve("foo", logger)).isEqualTo("bar");
        }
    }

    /**
     * Verifies that the {@code null} value of a JNDI name can be resolved.
     */
    @Test
    void resolveJndiNullValue() {
        try (MockedStatic<InitialContext> mock = mockStatic(InitialContext.class)) {
            mock.when(() -> InitialContext.doLookup("java:comp/env/foo")).thenReturn(null);

            JndiValueResolver resolver = new JndiValueResolver();
            assertThat(resolver.resolve("foo", logger)).isNull();
        }
    }

    /**
     * Verifies that {@code null} is returned for a non-existent JNDI name.
     */
    @Test
    void resolveMissingJndiName() {
        try (MockedStatic<InitialContext> mock = mockStatic(InitialContext.class)) {
            mock.when(() -> InitialContext.doLookup("java:comp/env/foo")).thenThrow(NameNotFoundException.class);

            JndiValueResolver resolver = new JndiValueResolver();
            assertThat(resolver.resolve("foo", logger)).isNull();
        }
    }

    /**
     * Verifies that {@code null} is returned and an error is logged, if the {@link InitialContext} throws any kind of
     * {@link NamingException} during lookup.
     */
    @Test
    void failResolvingInvalidJndiName() {
        try (MockedStatic<InitialContext> mock = mockStatic(InitialContext.class)) {
            mock.when(() -> InitialContext.doLookup("java:comp/env/foo")).thenThrow(InvalidNameException.class);

            JndiValueResolver resolver = new JndiValueResolver();
            assertThat(resolver.resolve("foo", logger)).isNull();
            assertThat(log.consume()).singleElement().satisfies(entry -> {
                assertThat(entry.getSeverityLevel()).isEqualTo(Level.ERROR);
                assertThat(entry.getThrowable()).isInstanceOf(InvalidNameException.class);
                assertThat(entry.getFormattedMessage(configuration)).contains("java:comp/env/foo");
            });
        }
    }

}
