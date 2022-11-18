package org.slf4j.impl;

import org.junit.jupiter.api.Test;
import org.slf4j.IMarkerFactory;
import org.slf4j.helpers.BasicMarkerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class StaticMarkerBinderTest {

    /**
     * Verifies that the static binder returns {@link BasicMarkerFactory} as marker factory.
     */
    @Test
    void provideMarkerFactoryInstance() {
        IMarkerFactory factory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
        assertThat(factory).isInstanceOf(BasicMarkerFactory.class);
    }

    /**
     * Verifies that the static binder returns the fully-qualified class name of {@link BasicMarkerFactory} as marker
     * factory class name.
     */
    @Test
    void provideMarkerFactoryClassName() {
        String className = StaticMarkerBinder.SINGLETON.getMarkerFactoryClassStr();
        assertThat(className).isEqualTo(BasicMarkerFactory.class.getName());
    }

}
