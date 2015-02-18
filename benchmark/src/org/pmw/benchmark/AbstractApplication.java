package org.pmw.benchmark;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.pmw.benchmark.frameworks.Dummy;
import org.pmw.benchmark.frameworks.Framework;
import org.pmw.benchmark.frameworks.JUL;
import org.pmw.benchmark.frameworks.Log4j;
import org.pmw.benchmark.frameworks.Log4j2;
import org.pmw.benchmark.frameworks.Logback;
import org.pmw.benchmark.frameworks.Tinylog;

public abstract class AbstractApplication {

	protected static final String[] FRAMEWORKS = new String[] { "dummy", "jul", "log4j", "log4j2", "logback", "tinylog" };
	protected static final String[] BENCHMARKS = new String[] { "output", "primes" };
	protected static final String[] THREADING = new String[] { "single-threaded", "multi-threaded" };

	protected static final Map<String, Class<? extends Framework>> FRAMEWORK_MAPPING = new HashMap<String, Class<? extends Framework>>();
	static {
		FRAMEWORK_MAPPING.put("dummy", Dummy.class);
		FRAMEWORK_MAPPING.put("jul", JUL.class);
		FRAMEWORK_MAPPING.put("log4j", Log4j.class);
		FRAMEWORK_MAPPING.put("log4j2", Log4j2.class);
		FRAMEWORK_MAPPING.put("logback", Logback.class);
		FRAMEWORK_MAPPING.put("tinylog", Tinylog.class);
	}

	protected static final boolean supportsAsync(final String framework) {
		return countBooleanParameters(framework) == 2;
	}

	protected static final Framework createFramework(final String name, final boolean locatioInformation, final boolean async)
			throws ReflectiveOperationException {
		int count = countBooleanParameters(name);
		if (count == 2) {
			return FRAMEWORK_MAPPING.get(name).getConstructor(boolean.class, boolean.class).newInstance(locatioInformation, async);
		} else if (count == 1) {
			return FRAMEWORK_MAPPING.get(name).getConstructor(boolean.class).newInstance(locatioInformation);
		} else {
			return FRAMEWORK_MAPPING.get(name).newInstance();
		}
	}

	private static final int countBooleanParameters(final String framework) {
		int count = 0;
		for (Constructor<?> constructor : FRAMEWORK_MAPPING.get(framework).getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == 1 && types[0] == boolean.class) {
				count = Math.max(count, 1);
			} else if (types.length == 2 && types[0] == boolean.class && types[1] == boolean.class) {
				count = Math.max(count, 2);
			}
		}
		return count;
	}

}
