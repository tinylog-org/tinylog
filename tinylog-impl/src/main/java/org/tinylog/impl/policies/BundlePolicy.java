package org.tinylog.impl.policies;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for bundling multiple {@link Policy} instances.
 */
public class BundlePolicy implements Policy {

    private final List<Policy> policies;

    /**
     * @param policies The policies to combine
     */
    public BundlePolicy(List<Policy> policies) {
        this.policies = new ArrayList<>(policies);
    }

    @Override
    public boolean canContinueFile(Path file) throws Exception {
        for (Policy policy : policies) {
            if (!policy.canContinueFile(file)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void init(Path file) throws Exception {
        Exception exception = null;

        for (Policy policy : policies) {
            try {
                policy.init(file);
            } catch (Exception ex) {
                if (exception == null) {
                    exception = ex;
                } else {
                    exception.addSuppressed(ex);
                }
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public boolean canAcceptLogEntry(int bytes) {
        for (Policy policy : policies) {
            if (!policy.canAcceptLogEntry(bytes)) {
                return false;
            }
        }

        return true;
    }

}
