package org.tinylog.core.runtime;

import java.util.function.Supplier;

import dalvik.system.VMStack;

/**
 * Supplier for receiving the caller class on legacy Android runtimes with support for {@link VMStack}.
 */
class VmStackClassSupplier implements Supplier<Object> {

    /** */
    VmStackClassSupplier() {
    }

    @Override
    public Object get() {
        return VMStack.getStackClass2();
    }

}
