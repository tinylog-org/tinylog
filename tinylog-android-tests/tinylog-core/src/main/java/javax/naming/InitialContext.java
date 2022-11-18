package javax.naming;

import javax.sql.DataSource;

/**
 * Skeleton for the real initial context.
 */
public class InitialContext {

    /**
     * Looks up the bound value for a JNDI name
     *
     * <p>
     *     This method is just a skeleton and throws always an {@link UnsupportedOperationException}.
     * </p>
     *
     * @throws NamingException Failed to look up the passed JNDI name
     * @param <T> The type of the bound value
     * @return The bound value
     */
    public static <T> T doLookup(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Binds an object to a name.
     *
     * @param name The context name for the passed object
     * @param object The object to bind
     * @throws NamingException Failed to bind the object
     */
    public void bind(String name, Object object) throws NamingException {
        throw new UnsupportedOperationException();
    }

    /**
     * Unbinds an object.
     *
     * @param name The context name of the object to unbind
     * @throws NamingException Failed to unbind the object
     */
    public void unbind(String name) throws NamingException {
        throw new UnsupportedOperationException();
    }

}
