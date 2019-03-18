/*
 * Copyright 2019 Martin Winandy
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

package org.tinylog.scala

import scala.reflect.macros.blackbox

/**
	* Macros for transforming calls of [[org.tinylog.scala.Logger]] into calls of [[org.tinylog.Logger]].
	*/
private object LoggerMacro {

	/**
		* Redirects [[org.tinylog.scala.Logger.isTraceEnabled]] to [[org.tinylog.Logger.isTraceEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isTraceEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.isTraceEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(message:Any)]] and [[org.tinylog.scala.Logger.trace(message:String)]]
		* to [[org.tinylog.Logger.trace(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def tracePlainMessage(context: blackbox.Context)
	                     (message: context.Expr[Any])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.trace(new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.trace($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(message:()=>String)]] to
		* [[org.tinylog.Logger.trace(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def traceLazyMessage(context: blackbox.Context)
	                    (message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.trace(new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.trace(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def traceMessageWithPlainArguments(context: blackbox.Context)
	                                  (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.trace($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.trace(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def traceMessageWithLazyArguments(context: blackbox.Context)
	                                 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.trace($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(exception:Throwable)]] to
		* [[org.tinylog.Logger.trace(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def traceException(context: blackbox.Context)
	                  (exception: context.Expr[Throwable])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.trace($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger.trace(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def traceExceptionWithPlainMessage(context: blackbox.Context)
	                                  (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.trace($exception, new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.trace($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger.trace(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def traceExceptionWithLazyMessage(context: blackbox.Context)
	                                 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.trace($exception, new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.trace(exception:Throwable,message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def traceExceptionWithMessageWithPlainArguments(context: blackbox.Context)
	                                               (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.trace($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.trace(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.trace(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def traceExceptionWithMessageWithLazyArguments(context: blackbox.Context)
	                                              (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.trace($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.isDebugEnabled]] to [[org.tinylog.Logger.isDebugEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isDebugEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.isDebugEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(message:Any)]] and [[org.tinylog.scala.Logger.debug(message:String)]]
		* to [[org.tinylog.Logger.debug(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def debugPlainMessage(context: blackbox.Context)
	                     (message: context.Expr[Any])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.debug(new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.debug($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(message:()=>String)]] to
		* [[org.tinylog.Logger.debug(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def debugLazyMessage(context: blackbox.Context)
	                    (message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.debug(new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.debug(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def debugMessageWithPlainArguments(context: blackbox.Context)
	                                  (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.debug($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.debug(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def debugMessageWithLazyArguments(context: blackbox.Context)
	                                 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.debug($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(exception:Throwable)]] to
		* [[org.tinylog.Logger.debug(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def debugException(context: blackbox.Context)
	                  (exception: context.Expr[Throwable])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.debug($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger.debug(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def debugExceptionWithPlainMessage(context: blackbox.Context)
	                                  (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.debug($exception, new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.debug($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger.debug(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def debugExceptionWithLazyMessage(context: blackbox.Context)
	                                 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.debug($exception, new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.debug(exception:Throwable,message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def debugExceptionWithMessageWithPlainArguments(context: blackbox.Context)
	                                               (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.debug($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.debug(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.debug(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def debugExceptionWithMessageWithLazyArguments(context: blackbox.Context)
	                                              (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.debug($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.isInfoEnabled]] to [[org.tinylog.Logger.isInfoEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isInfoEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.isInfoEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(message:Any)]] and [[org.tinylog.scala.Logger.info(message:String)]]
		* to [[org.tinylog.Logger.info(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def infoPlainMessage(context: blackbox.Context)
	                     (message: context.Expr[Any])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.info(new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.info($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(message:()=>String)]] to
		* [[org.tinylog.Logger.info(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def infoLazyMessage(context: blackbox.Context)
	                    (message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.info(new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.info(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def infoMessageWithPlainArguments(context: blackbox.Context)
	                                  (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.info($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.info(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def infoMessageWithLazyArguments(context: blackbox.Context)
	                                 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.info($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(exception:Throwable)]] to
		* [[org.tinylog.Logger.info(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def infoException(context: blackbox.Context)
	                  (exception: context.Expr[Throwable])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.info($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger.info(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def infoExceptionWithPlainMessage(context: blackbox.Context)
	                                  (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.info($exception, new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.info($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger.info(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def infoExceptionWithLazyMessage(context: blackbox.Context)
	                                 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.info($exception, new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.info(exception:Throwable,message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def infoExceptionWithMessageWithPlainArguments(context: blackbox.Context)
	                                               (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.info($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.info(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.info(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def infoExceptionWithMessageWithLazyArguments(context: blackbox.Context)
	                                              (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.info($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.isWarnEnabled]] to [[org.tinylog.Logger.isWarnEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isWarnEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.isWarnEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(message:Any)]] and [[org.tinylog.scala.Logger.warn(message:String)]]
		* to [[org.tinylog.Logger.warn(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def warnPlainMessage(context: blackbox.Context)
	                     (message: context.Expr[Any])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.warn(new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.warn($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(message:()=>String)]] to
		* [[org.tinylog.Logger.warn(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def warnLazyMessage(context: blackbox.Context)
	                    (message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.warn(new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.warn(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def warnMessageWithPlainArguments(context: blackbox.Context)
	                                  (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.warn($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.warn(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def warnMessageWithLazyArguments(context: blackbox.Context)
	                                 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.warn($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(exception:Throwable)]] to
		* [[org.tinylog.Logger.warn(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def warnException(context: blackbox.Context)
	                  (exception: context.Expr[Throwable])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.warn($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger.warn(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def warnExceptionWithPlainMessage(context: blackbox.Context)
	                                  (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.warn($exception, new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.warn($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger.warn(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def warnExceptionWithLazyMessage(context: blackbox.Context)
	                                 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.warn($exception, new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.warn(exception:Throwable,message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def warnExceptionWithMessageWithPlainArguments(context: blackbox.Context)
	                                               (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.warn($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.warn(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.warn(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def warnExceptionWithMessageWithLazyArguments(context: blackbox.Context)
	                                              (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.warn($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.isErrorEnabled]] to [[org.tinylog.Logger.isErrorEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isErrorEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.isErrorEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(message:Any)]] and [[org.tinylog.scala.Logger.error(message:String)]]
		* to [[org.tinylog.Logger.error(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def errorPlainMessage(context: blackbox.Context)
	                     (message: context.Expr[Any])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.error(new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.error($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(message:()=>String)]] to
		* [[org.tinylog.Logger.error(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def errorLazyMessage(context: blackbox.Context)
	                    (message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.error(new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.error(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def errorMessageWithPlainArguments(context: blackbox.Context)
	                                  (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.error($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.error(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def errorMessageWithLazyArguments(context: blackbox.Context)
	                                 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.error($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(exception:Throwable)]] to
		* [[org.tinylog.Logger.error(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def errorException(context: blackbox.Context)
	                  (exception: context.Expr[Throwable])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.error($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger.error(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def errorExceptionWithPlainMessage(context: blackbox.Context)
	                                  (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Tree = {
		import context.universe._
		if (message.tree.toString().contains("scala.StringContext")) {
			q"org.tinylog.Logger.error($exception, new Supplier[String] { override def get(): String = $message })"
		} else {
			q"org.tinylog.Logger.error($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger.error(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def errorExceptionWithLazyMessage(context: blackbox.Context)
	                                 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.error($exception, new Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger.error(exception:Throwable,message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def errorExceptionWithMessageWithPlainArguments(context: blackbox.Context)
	                                               (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Tree = {
		import context.universe._
		q"org.tinylog.Logger.error($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.Logger.error(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger.error(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def errorExceptionWithMessageWithLazyArguments(context: blackbox.Context)
	                                              (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Tree = {
		import context.universe._
		val suppliers = arguments.map(argument => q"new Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"org.tinylog.Logger.error($exception, $message, ..$suppliers)"
	}

}
