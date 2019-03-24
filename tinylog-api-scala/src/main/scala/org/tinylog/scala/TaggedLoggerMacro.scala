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
	* Macros for transforming calls of [[org.tinylog.scala.TaggedLogger]] into calls of [[org.tinylog.TaggedLogger]].
	*/
private object TaggedLoggerMacro {

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isTraceEnabled]] to [[org.tinylog.TaggedLogger#isTraceEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isTraceEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		val logger = q"${context.prefix}.logger"
		q"$logger.isTraceEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(message:Any)]] and [[org.tinylog.scala.TaggedLogger#trace(message:String)]]
		* to [[org.tinylog.TaggedLogger#trace(message:Any)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.trace(new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.trace($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#trace(message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.trace(new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#trace(message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.trace($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#trace(message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.trace($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(exception:Throwable)]] to
		* [[org.tinylog.TaggedLogger#trace(exception:Throwable)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.trace($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(exception:Throwable,message:String)]] to
		* [[org.tinylog.TaggedLogger#trace(exception:Throwable,message:String)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.trace($exception, new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.trace($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#trace(exception:Throwable,message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.trace($exception, new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#trace(exception:Throwable,message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.trace($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#trace(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#trace(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.trace($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isDebugEnabled]] to [[org.tinylog.TaggedLogger#isDebugEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isDebugEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		val logger = q"${context.prefix}.logger"
		q"$logger.isDebugEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(message:Any)]] and [[org.tinylog.scala.TaggedLogger#debug(message:String)]]
		* to [[org.tinylog.TaggedLogger#debug(message:Any)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.debug(new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.debug($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#debug(message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.debug(new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#debug(message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.debug($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#debug(message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.debug($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(exception:Throwable)]] to
		* [[org.tinylog.TaggedLogger#debug(exception:Throwable)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.debug($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(exception:Throwable,message:String)]] to
		* [[org.tinylog.TaggedLogger#debug(exception:Throwable,message:String)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.debug($exception, new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.debug($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#debug(exception:Throwable,message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.debug($exception, new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#debug(exception:Throwable,message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.debug($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#debug(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#debug(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.debug($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isInfoEnabled]] to [[org.tinylog.TaggedLogger#isInfoEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isInfoEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		val logger = q"${context.prefix}.logger"
		q"$logger.isInfoEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(message:Any)]] and [[org.tinylog.scala.TaggedLogger#info(message:String)]]
		* to [[org.tinylog.TaggedLogger#info(message:Any)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.info(new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.info($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#info(message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.info(new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#info(message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.info($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#info(message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.info($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(exception:Throwable)]] to
		* [[org.tinylog.TaggedLogger#info(exception:Throwable)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.info($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(exception:Throwable,message:String)]] to
		* [[org.tinylog.TaggedLogger#info(exception:Throwable,message:String)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.info($exception, new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.info($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#info(exception:Throwable,message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.info($exception, new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#info(exception:Throwable,message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.info($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#info(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#info(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.info($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isWarnEnabled]] to [[org.tinylog.TaggedLogger#isWarnEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isWarnEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		val logger = q"${context.prefix}.logger"
		q"$logger.isWarnEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(message:Any)]] and [[org.tinylog.scala.TaggedLogger#warn(message:String)]]
		* to [[org.tinylog.TaggedLogger#warn(message:Any)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.warn(new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.warn($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#warn(message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.warn(new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#warn(message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.warn($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#warn(message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.warn($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(exception:Throwable)]] to
		* [[org.tinylog.TaggedLogger#warn(exception:Throwable)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.warn($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(exception:Throwable,message:String)]] to
		* [[org.tinylog.TaggedLogger#warn(exception:Throwable,message:String)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.warn($exception, new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.warn($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#warn(exception:Throwable,message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.warn($exception, new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#warn(exception:Throwable,message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.warn($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#warn(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#warn(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.warn($exception, $message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isErrorEnabled]] to [[org.tinylog.TaggedLogger#isErrorEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isErrorEnabled(context: blackbox.Context)
	                  ()
	: context.universe.Tree = {
		import context.universe._
		val logger = q"${context.prefix}.logger"
		q"$logger.isErrorEnabled()"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(message:Any)]] and [[org.tinylog.scala.TaggedLogger#error(message:String)]]
		* to [[org.tinylog.TaggedLogger#error(message:Any)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.error(new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.error($message.asInstanceOf[Any])"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#error(message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.error(new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#error(message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.error($message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#error(message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.error($message, ..$suppliers)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(exception:Throwable)]] to
		* [[org.tinylog.TaggedLogger#error(exception:Throwable)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.error($exception)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(exception:Throwable,message:String)]] to
		* [[org.tinylog.TaggedLogger#error(exception:Throwable,message:String)]].
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
		val logger = q"${context.prefix}.logger"
		if (message.tree.toString().contains("scala.StringContext")) {
			q"$logger.error($exception, new org.tinylog.Supplier[String] { override def get(): String = $message })"
		} else {
			q"$logger.error($exception, $message)"
		}
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.TaggedLogger#error(exception:Throwable,message:org.tinylog.Supplier)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.error($exception, new org.tinylog.Supplier[String] { override def get(): String = $message.apply() })"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.TaggedLogger#error(exception:Throwable,message:String,arguments:Any*)]].
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
		val logger = q"${context.prefix}.logger"
		q"$logger.error($exception, $message, ..$arguments)"
	}

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#error(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.TaggedLogger#error(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
		val logger = q"${context.prefix}.logger"
		val suppliers = arguments.map(argument => q"new org.tinylog.Supplier[Any] { override def get(): Any = $argument.apply() }")
		q"$logger.error($exception, $message, ..$suppliers)"
	}

}
