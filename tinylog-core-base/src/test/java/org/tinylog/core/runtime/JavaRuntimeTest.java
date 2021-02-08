package org.tinylog.core.runtime;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Duration;
import java.util.function.Supplier;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;
import org.tinylog.core.Level;
import org.tinylog.core.test.log.CaptureLogEntries;
import org.tinylog.core.test.log.Log;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@CaptureLogEntries
class JavaRuntimeTest {

	@Inject
	private Log log;

	/**
	 * Verifies that a valid process ID is provided.
	 */
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void validProcessId() {
		long pid = new JavaRuntime().getProcessId();
		assertThat(pid).isGreaterThan(0);
	}

	/**
	 * Verifies that a meaningful error will be logged, if the process ID cannot be resolved from
	 * {@link RuntimeMXBean#getName()} on Java 8.
	 *
	 * @param runtimeName The invalid runtime name to test
	 */
	@EnabledForJreRange(max = JRE.JAVA_8)
	@ParameterizedTest
	@ValueSource(strings = {"bar", "foo@localhost"})
	void invalidProcessId(String runtimeName) {
		RuntimeMXBean bean = mock(RuntimeMXBean.class);
		when(bean.getName()).thenReturn(runtimeName);

		try (MockedStatic<ManagementFactory> factory = mockStatic(ManagementFactory.class)) {
			factory.when(ManagementFactory::getRuntimeMXBean).thenReturn(bean);

			long pid = new JavaRuntime().getProcessId();
			assertThat(pid).isEqualTo(-1);
			assertThat(log.consume()).anySatisfy(entry -> {
				assertThat(entry.getLevel()).isEqualTo(Level.ERROR);
				assertThat(entry.getMessage()).contains(runtimeName);
			});
		}
	}

	/**
	 * Verifies that valid uptime values are provided.
	 */
	@DisabledIfSystemProperty(named = "java.runtime.name", matches = "Android Runtime")
	@Test
	void uptime() throws InterruptedException {
		JavaRuntime runtime = new JavaRuntime();

		Duration time1 = runtime.getUptime();
		assertThat(time1).isBetween(Duration.ZERO, Duration.ofHours(1));

		Thread.sleep(10);

		Duration time2 = runtime.getUptime();
		assertThat(time2).isGreaterThan(time1);
	}

	/**
	 * Verifies that a valid stack location can be extracted from a defined index.
	 */
	@Test
	void stackTraceLocationAtIndex() {
		StackTraceLocation location = new JavaRuntime().getStackTraceLocationAtIndex(0);
		assertThat(location).isInstanceOf(JavaIndexBasedStackTraceLocation.class);
		assertThat(location.getCallerClassName()).isEqualTo(JavaRuntimeTest.class.getName());
	}

	/**
	 * Verifies that a valid stack location can be extracted via a passed callee class name.
	 */
	@Test
	void stackTraceLocationAfterClass() {
		StackTraceLocation location = new JavaRuntime().getStackTraceLocationAfterClass(Callee.class.getName());
		assertThat(location).isInstanceOf(JavaClassNameBasedStackTraceLocation.class);

		String className = Callee.execute(() -> getCallerClassName(location.push()));
		assertThat(className).isEqualTo(JavaRuntimeTest.class.getName());
	}

	/**
	 * Retrieves the caller class name from the passed stack trace location.
	 *
	 * @param location The stack trace location from which the caller is to be received
	 * @return The received caller class name
	 */
	private String getCallerClassName(StackTraceLocation location) {
		return location.getCallerClassName();
	}

	/**
	 * Helper class to simulate a callee.
	 */
	private static final class Callee {

		/**
		 * Executes the passed {@link Supplier}.
		 *
		 * @param supplier The supplier to execute
		 * @param <T> Return type
		 * @return The produced value from the passed supplier
		 */
		static <T> T execute(Supplier<T> supplier) {
			return supplier.get();
		}

	}

}
