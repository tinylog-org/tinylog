package javax.naming;

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
	 * @throws NamingException Failed ot look up the passed JNDI name
	 * @return The bound value
	 */
	public static Object doLookup(String name) throws NamingException {
		throw new UnsupportedOperationException();
	}

}
