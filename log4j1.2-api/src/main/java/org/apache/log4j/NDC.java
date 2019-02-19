/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import java.util.Stack;

import org.tinylog.provider.InternalLogger;

/**
 * The NDC class implements <i>nested diagnostic contexts</i> as defined by Neil Harrison in the article "Patterns for
 * Logging Diagnostic Messages" part of the book "<i>Pattern Languages of Program Design 3</i>" edited by Martin et al.
 * 
 * <p>
 * A Nested Diagnostic Context, or NDC in short, is an instrument to distinguish interleaved log output from different
 * sources. Log output is typically interleaved when a server handles multiple clients near-simultaneously.
 * </p>
 * 
 * <p>
 * Interleaved log output can still be meaningful if each log entry from different contexts had a distinctive stamp.
 * This is where NDCs come into play.
 * </p>
 * 
 * <p>
 * <em><b>Note that NDCs are managed on a per thread basis</b></em>. NDC operations such as {@link #push push},
 * {@link #pop}, {@link #clear}, {@link #getDepth} and {@link #setMaxDepth} affect the NDC of the <em>current</em>
 * thread only. NDCs of other threads remain unaffected.
 * </p>
 * 
 * <p>
 * For example, a servlet can build a per client request NDC consisting the clients host name and other information
 * contained in the the request. <em>Cookies</em> are another source of distinctive information. To build an NDC one
 * uses the {@link #push push} operation. Simply put,
 * </p>
 * 
 * <ul>
 * <li>Contexts can be nested.</li>
 * <li>When entering a context, call {@code NDC.push}. As a side effect, if there is no nested diagnostic context for
 * the current thread, this method will create it.</li>
 * <li>When leaving a context, call {@code NDC.pop}.</li>
 * <li><b>When exiting a thread make sure to call {@link #remove NDC.remove()}</b>.</li>
 * </ul>
 * 
 * <p>
 * There is no penalty for forgetting to match each {@code push} operation with a corresponding {@code pop}, except the
 * obvious mismatch between the real application context and the context set in the NDC.
 * </p>
 * 
 * <p>
 * Heavy duty systems should call the {@link #remove} method when leaving the run method of a thread. This ensures that
 * the memory used by the thread can be freed by the Java garbage collector. There is a mechanism to lazily remove
 * references to dead threads. In practice, this means that you can be a little sloppy and sometimes forget to call
 * {@link #remove} before exiting a thread.
 * </p>
 * 
 * <p>
 * A thread may inherit the nested diagnostic context of another (possibly parent) thread using the {@link #inherit
 * inherit} method. A thread may obtain a copy of its NDC with the {@link #cloneStack cloneStack} method and pass the
 * reference to any other thread, in particular to a child.
 * </p>
 * 
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.7.0
 */

public final class NDC {

	/** */
	private NDC() {
	}

	/**
	 * Clear any nested diagnostic information if any. This method is useful in cases where the same thread can be
	 * potentially used over and over in different unrelated contexts.
	 * 
	 * <p>
	 * This method is equivalent to calling the {@link #setMaxDepth} method with a zero {@code maxDepth} argument.
	 * </p>
	 * 
	 * @since 0.8.4c
	 */
	public static void clear() {
		// Ignore
	}

	/**
	 * Clone the diagnostic context for the current thread.
	 * 
	 * <p>
	 * Internally a diagnostic context is represented as a stack. A given thread can supply the stack (i.e. diagnostic
	 * context) to a child thread so that the child can inherit the parent thread's diagnostic context.
	 * </p>
	 * 
	 * <p>
	 * The child thread uses the {@link #inherit inherit} method to inherit the parent's diagnostic context.
	 * </p>
	 * 
	 * @return Stack A clone of the current thread's diagnostic context.
	 */
	@SuppressWarnings("rawtypes")
	public static Stack cloneStack() {
		return null;
	}

	/**
	 * Inherit the diagnostic context of another thread.
	 * 
	 * <p>
	 * The parent thread can obtain a reference to its diagnostic context using the {@link #cloneStack} method. It
	 * should communicate this information to its child so that it may inherit the parent's diagnostic context.
	 * </p>
	 * 
	 * <p>
	 * The parent's diagnostic context is cloned before being inherited. In other words, once inherited, the two
	 * diagnostic contexts can be managed independently.
	 * </p>
	 * 
	 * <p>
	 * In java, a child thread cannot obtain a reference to its parent, unless it is directly handed the reference.
	 * Consequently, there is no client-transparent way of inheriting diagnostic contexts. Do you know any solution to
	 * this problem?
	 * </p>
	 * 
	 * @param stack
	 *            The diagnostic context of the parent thread.
	 */
	@SuppressWarnings("rawtypes")
	public static void inherit(final Stack stack) {
		// Ignore
	}

	/**
	 * Get the current nesting depth of this diagnostic context.
	 *
	 * @return Current nesting depth
	 * @see #setMaxDepth
	 * @since 0.7.5
	 */
	public static int getDepth() {
		return 0;
	}

	/**
	 * Clients should call this method before leaving a diagnostic context.
	 * 
	 * <p>
	 * The returned value is the value that was pushed last. If no context is available, then the empty string "" is
	 * returned.
	 * </p>
	 * 
	 * @return String The innermost diagnostic context.
	 */
	public static String pop() {
		return "";
	}

	/**
	 * Looks at the last diagnostic context at the top of this NDC without removing it.
	 * 
	 * <p>
	 * The returned value is the value that was pushed last. If no context is available, then the empty string "" is
	 * returned.
	 * </p>
	 * 
	 * @return The innermost diagnostic context.
	 */
	public static String peek() {
		return "";
	}

	/**
	 * Push new diagnostic context information for the current thread.
	 * 
	 * <p>
	 * The contents of the {@code message} parameter is determined solely by the client.
	 * </p>
	 * 
	 * @param message
	 *            The new diagnostic context information.
	 */
	public static void push(final String message) {
		InternalLogger.log(org.tinylog.Level.WARN, "tinylog does not support NDC, \"" + message + "\" will be discarded");
	}

	/**
	 * Remove the diagnostic context for this thread.
	 * 
	 * <p>
	 * Each thread that created a diagnostic context by calling {@link #push} should call this method before exiting.
	 * Otherwise, the memory used by the <b>thread</b> cannot be reclaimed by the VM.
	 * </p>
	 * 
	 * <p>
	 * As this is such an important problem in heavy duty systems and because it is difficult to always guarantee that
	 * the remove method is called before exiting a thread, this method has been augmented to lazily remove references
	 * to dead threads. In practice, this means that you can be a little sloppy and occasionally forget to call
	 * {@link #remove} before exiting a thread. However, you must call {@code remove} sometime. If you never call it,
	 * then your application is sure to run out of memory.
	 * </p>
	 */
	public static void remove() {
		// Ignore
	}

	/**
	 * Set maximum depth of this diagnostic context. If the current depth is smaller or equal to {@code maxDepth}, then
	 * no action is taken.
	 * 
	 * <p>
	 * This method is a convenient alternative to multiple {@link #pop} calls. Moreover, it is often the case that at
	 * the end of complex call sequences, the depth of the NDC is unpredictable. The {@code setMaxDepth} method
	 * circumvents this problem.
	 * </p>
	 * 
	 * <p>
	 * For example, the combination
	 * </p>
	 * 
	 * <pre>
	 * void foo() {
	 * &nbsp;  int depth = NDC.getDepth();
	 * &nbsp;  ... complex sequence of calls
	 * &nbsp;  NDC.setMaxDepth(depth);
	 * }
	 * </pre>
	 * 
	 * <p>
	 * ensures that between the entry and exit of foo the depth of the diagnostic stack is conserved.
	 * </p>
	 * 
	 * @param maxDepth
	 *            New maximum depth
	 * @see #getDepth
	 * @since 0.7.5
	 */
	public static void setMaxDepth(final int maxDepth) {
		// Ignore
	}

}
