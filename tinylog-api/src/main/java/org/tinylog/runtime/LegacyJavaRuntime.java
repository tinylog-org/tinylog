/*
 * Copyright 2016 Martin Winandy
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.tinylog.runtime;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.tinylog.Level;
import org.tinylog.provider.InternalLogger;

/**
 * Runtime dialect implementation for Java 6-8.
 */
public final class LegacyJavaRuntime extends AbstractJavaRuntime {

	private static final Timestamp startTime = new LegacyTimestamp(System.currentTimeMillis());

	private final boolean hasSunReflection;
	private final Method stackTraceElementGetter;

	/** */
	public LegacyJavaRuntime() {
		hasSunReflection = verifySunReflection();
		stackTraceElementGetter = getStackTraceElementGetter();
	}

	@Override
	public boolean isAndroid() {
		return false;
	}

	@Override
	public long getProcessId() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		try {
			return Long.parseLong(name.substring(0, name.indexOf('@')));
		} catch (NumberFormatException ex) {
			InternalLogger.log(Level.ERROR, "Illegal process ID: " + name.substring(0, name.indexOf('@')));
			return -1;
		} catch (IndexOutOfBoundsException ex) {
			InternalLogger.log(Level.ERROR, "Name of virtual machine does not contain a process ID: " + name);
			return -1;
		}
	}

	@Override
	public Timestamp getStartTime() {
		return startTime;
	}

	@Override
	@SuppressWarnings("removal")
	@IgnoreJRERequirement
	public String getCallerClassName(final int depth) {
		if (hasSunReflection) {
			return sun.reflect.Reflection.getCallerClass(depth + 1).getName();
		} else {
			return getCallerStackTraceElement(depth + 1).getClassName();
		}
	}

	@Override
	public String getCallerClassName(final String loggerClassName) {
		StackTraceElement element = getCallerStackTraceElement(loggerClassName);
		return element == null ? null : element.getClassName();
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final int depth) {
		if (stackTraceElementGetter != null) {
			try {
				return (StackTraceElement) stackTraceElementGetter.invoke(new Throwable(), depth);
			} catch (IllegalAccessException ex) {
				InternalLogger.log(Level.ERROR, ex, "Failed getting single stack trace element from throwable");
			} catch (InvocationTargetException ex) {
				InternalLogger.log(Level.ERROR, ex.getTargetException(), "Failed getting single stack trace element from throwable");
			}
		}

		return new Throwable().getStackTrace()[depth];
	}

	@Override
	public StackTraceElement getCallerStackTraceElement(final String loggerClassName) {
		StackTraceElement[] trace = new Throwable().getStackTrace();
		int index = 0;
		
		while (index < trace.length && !loggerClassName.equals(trace[index].getClassName())) {
			index = index + 1;
		}
		
		while (index < trace.length && loggerClassName.equals(trace[index].getClassName())) {
			index = index + 1;
		}
		
		if (index < trace.length) {
			return trace[index];
		} else {
			return null;
		}
	}

	@Override
	public Timestamp createTimestamp() {
		return new LegacyTimestamp();
	}

	@Override
	public TimestampFormatter createTimestampFormatter(final String pattern, final Locale locale) {
		return new LegacyTimestampFormatter(pattern, locale);
	}

	/**
	 * Checks whether {@link sun.reflect.Reflection#getCallerClass(int)} is available.
	 *
	 * @return {@code true} if available, {@code false} if not
	 */
	@SuppressWarnings("removal")
	@IgnoreJRERequirement
	private static boolean verifySunReflection() {
		try {
			return AbstractJavaRuntime.class.equals(sun.reflect.Reflection.getCallerClass(1));
		} catch (NoClassDefFoundError error) {
			return false;
		} catch (NoSuchMethodError error) {
			return false;
		} catch (UnsatisfiedLinkError error) {
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Gets {@link Throwable#getStackTraceElement(int)} as accessible method.
	 *
	 * @return Instance if available, {@code null} if not
	 */
	private static Method getStackTraceElementGetter() {
		try {
			Method method = Throwable.class.getDeclaredMethod("getStackTraceElement", int.class);
			method.setAccessible(true);
			StackTraceElement stackTraceElement = (StackTraceElement) method.invoke(new Throwable(), 0);
			if (LegacyJavaRuntime.class.getName().equals(stackTraceElement.getClassName())) {
				return method;
			} else {
				return null;
			}
		} catch (Exception ex) {
			return null;
		}
	}

}
