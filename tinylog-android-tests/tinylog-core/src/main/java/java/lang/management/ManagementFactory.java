package java.lang.management;

/**
 * Skeleton for the real management factory.
 */
public class ManagementFactory {

    /** */
    private ManagementFactory() {
    }

    /**
     * Gets an instance the runtime MX bean.
     *
     * @return Runtime MX bean instance
     */
    public static RuntimeMXBean getRuntimeMXBean() {
        return new RuntimeMXBean();
    }

}
