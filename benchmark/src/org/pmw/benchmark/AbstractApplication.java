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
		for (Constructor<?> constructor : FRAMEWORK_MAPPING.get(framework).getDeclaredConstructors()) {
			Class<?>[] types = constructor.getParameterTypes();
			if (types.length == 1 && types[0] == boolean.class) {
				return true;
			}
		}
		return false;
	}

	protected static final Framework createFramework(final String name, final boolean async) throws ReflectiveOperationException {
		if (supportsAsync(name)) {
			return FRAMEWORK_MAPPING.get(name).getConstructor(boolean.class).newInstance(async);
		} else {
			return FRAMEWORK_MAPPING.get(name).newInstance();
		}
	}

}
