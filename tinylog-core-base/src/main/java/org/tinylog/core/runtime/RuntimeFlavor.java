package org.tinylog.core.runtime;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.tinylog.core.backend.OutputDetails;

/**
 * Abstraction of API that depends on the Java version or flavor.
 */
public interface RuntimeFlavor {

	/**
	 * Gets the process ID of the current process.
	 *
	 * @return The process ID of the current process
	 */
	long getProcessId();

	/**
	 * Gets the uptime of the application.
	 *
	 * @return The uptime of the application
	 */
	Duration getUptime();

	/**
	 * Gets the name of the default writer to use if none is explicitly configured.
	 *
	 * @return The name of the default writer
	 */
	String getDefaultWriter();

	/**
	 * Gets a supplier that resolves the location information of the direct caller. The direct caller is the caller
	 * of the method that resolves the returned supplier. The result type of the supplier depends on the passed required
	 * output details.
	 *
	 * <table>
	 *     <caption>Output Details</caption>
	 *     <tr>
	 *         <th>Passed required output details</th>
	 *         <th>Supplier result type</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#DISABLED}</td>
	 *         <td>{@code null}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}</td>
	 *         <td>{@code null}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME}</td>
	 *         <td>{@link Class} or class name as {@link String}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION}</td>
	 *         <td>{@link StackTraceElement}</td>
	 *     </tr>
	 * </table>
	 *
	 * @param outputDetails The required location information
	 * @return The supplier for resolving the direct caller
	 */
	Supplier<Object> getDirectCaller(OutputDetails outputDetails);

	/**
	 * Gets a function that resolves the location information of a relative caller. The relative caller is the caller
	 * of the passed fully-qualified class name. The result type of the function depends on the passed required output
	 * details.
	 *
	 * <table>
	 *     <caption>Output Details</caption>
	 *     <tr>
	 *         <th>Passed required output details</th>
	 *         <th>Function result type</th>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#DISABLED}</td>
	 *         <td>{@code null}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#ENABLED_WITHOUT_LOCATION_INFORMATION}</td>
	 *         <td>{@code null}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#ENABLED_WITH_CALLER_CLASS_NAME}</td>
	 *         <td>{@link Class} or class name as {@link String}</td>
	 *     </tr>
	 *     <tr>
	 *         <td>{@link OutputDetails#ENABLED_WITH_FULL_LOCATION_INFORMATION}</td>
	 *         <td>{@link StackTraceElement}</td>
	 *     </tr>
	 * </table>
	 *
	 * @param outputDetails The required location information
	 * @return The function for resolving the caller of the class having the passed fully-qualified class name
	 */
	Function<String, Object> getRelativeCaller(OutputDetails outputDetails);

}
