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

import org.tinylog.Supplier

import scala.reflect.macros.blackbox

/**
	* Macros for transforming calls of [[org.tinylog.scala.Logger]] into calls of [[org.tinylog.Logger]].
	*/
private object LoggerMacro {

	private type LoggerContext = blackbox.Context

	/**
		* Redirects [[org.tinylog.scala.Logger#isTraceEnabled]] to [[org.tinylog.Logger#isTraceEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isTraceEnabled(context: LoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		org.tinylog.Logger.isTraceEnabled
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(message:Any)]] and [[org.tinylog.scala.Logger#trace(message:String)]]
		* to [[org.tinylog.Logger#trace(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def tracePlainMessage(context: LoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.trace(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.trace(message.splice.asInstanceOf[Any])
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(message:()=>String)]] to
		* [[org.tinylog.Logger#trace(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def traceLazyMessage(context: LoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#trace(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def traceMessageWithPlainArguments(context: LoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#trace(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def traceMessageWithLazyArguments(context: LoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(exception:Throwable)]] to
		* [[org.tinylog.Logger#trace(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def traceException(context: LoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(exception.splice)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger#trace(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def traceExceptionWithPlainMessage(context: LoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.trace(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.trace(exception.splice, message.splice)
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger#trace(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def traceExceptionWithLazyMessage(context: LoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#trace(exception:Throwable,message:String,arguments:Any*)]].
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
	def traceExceptionWithMessageWithPlainArguments(context: LoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#trace(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#trace(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
	def traceExceptionWithMessageWithLazyArguments(context: LoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.trace(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#isDebugEnabled]] to [[org.tinylog.Logger#isDebugEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isDebugEnabled(context: LoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		org.tinylog.Logger.isDebugEnabled
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(message:Any)]] and [[org.tinylog.scala.Logger#debug(message:String)]]
		* to [[org.tinylog.Logger#debug(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def debugPlainMessage(context: LoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.debug(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.debug(message.splice.asInstanceOf[Any])
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(message:()=>String)]] to
		* [[org.tinylog.Logger#debug(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def debugLazyMessage(context: LoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#debug(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def debugMessageWithPlainArguments(context: LoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#debug(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def debugMessageWithLazyArguments(context: LoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(exception:Throwable)]] to
		* [[org.tinylog.Logger#debug(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def debugException(context: LoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(exception.splice)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger#debug(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def debugExceptionWithPlainMessage(context: LoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.debug(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.debug(exception.splice, message.splice)
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger#debug(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def debugExceptionWithLazyMessage(context: LoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#debug(exception:Throwable,message:String,arguments:Any*)]].
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
	def debugExceptionWithMessageWithPlainArguments(context: LoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#debug(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#debug(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
	def debugExceptionWithMessageWithLazyArguments(context: LoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.debug(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#isInfoEnabled]] to [[org.tinylog.Logger#isInfoEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isInfoEnabled(context: LoggerContext)
									 ()
	: context.universe.Expr[Boolean] = context.universe.reify(
		org.tinylog.Logger.isInfoEnabled
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(message:Any)]] and [[org.tinylog.scala.Logger#info(message:String)]]
		* to [[org.tinylog.Logger#info(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def infoPlainMessage(context: LoggerContext)
											(message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.info(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.info(message.splice.asInstanceOf[Any])
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#info(message:()=>String)]] to
		* [[org.tinylog.Logger#info(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def infoLazyMessage(context: LoggerContext)
										 (message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#info(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def infoMessageWithPlainArguments(context: LoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#info(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def infoMessageWithLazyArguments(context: LoggerContext)
																	(message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(exception:Throwable)]] to
		* [[org.tinylog.Logger#info(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def infoException(context: LoggerContext)
									 (exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(exception.splice)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger#info(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def infoExceptionWithPlainMessage(context: LoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.info(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.info(exception.splice, message.splice)
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#info(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger#info(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def infoExceptionWithLazyMessage(context: LoggerContext)
																	(exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#info(exception:Throwable,message:String,arguments:Any*)]].
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
	def infoExceptionWithMessageWithPlainArguments(context: LoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#info(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#info(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
	def infoExceptionWithMessageWithLazyArguments(context: LoggerContext)
																							 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.info(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#isWarnEnabled]] to [[org.tinylog.Logger#isWarnEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isWarnEnabled(context: LoggerContext)
									 ()
	: context.universe.Expr[Boolean] = context.universe.reify(
		org.tinylog.Logger.isWarnEnabled
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(message:Any)]] and [[org.tinylog.scala.Logger#warn(message:String)]]
		* to [[org.tinylog.Logger#warn(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def warnPlainMessage(context: LoggerContext)
											(message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.warn(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.warn(message.splice.asInstanceOf[Any])
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(message:()=>String)]] to
		* [[org.tinylog.Logger#warn(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def warnLazyMessage(context: LoggerContext)
										 (message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#warn(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def warnMessageWithPlainArguments(context: LoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#warn(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def warnMessageWithLazyArguments(context: LoggerContext)
																	(message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(exception:Throwable)]] to
		* [[org.tinylog.Logger#warn(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def warnException(context: LoggerContext)
									 (exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(exception.splice)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger#warn(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def warnExceptionWithPlainMessage(context: LoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.warn(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.warn(exception.splice, message.splice)
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger#warn(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def warnExceptionWithLazyMessage(context: LoggerContext)
																	(exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#warn(exception:Throwable,message:String,arguments:Any*)]].
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
	def warnExceptionWithMessageWithPlainArguments(context: LoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#warn(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#warn(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
	def warnExceptionWithMessageWithLazyArguments(context: LoggerContext)
																							 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.warn(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#isErrorEnabled]] to [[org.tinylog.Logger#isErrorEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isErrorEnabled(context: LoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		org.tinylog.Logger.isErrorEnabled
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(message:Any)]] and [[org.tinylog.scala.Logger#error(message:String)]]
		* to [[org.tinylog.Logger#error(message:Any)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message object
		* @return Replaced source code
		*/
	def errorPlainMessage(context: LoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.error(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.error(message.splice.asInstanceOf[Any])
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#error(message:()=>String)]] to
		* [[org.tinylog.Logger#error(message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def errorLazyMessage(context: LoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#error(message:String,arguments:Any*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed arguments for formatted text message
		* @return Replaced source code
		*/
	def errorMessageWithPlainArguments(context: LoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#error(message:String,arguments:org.tinylog.Supplier*)]].
		*
		* @param context
		* Macro context
		* @param message
		* Passed formatted text message with placeholders
		* @param arguments
		* Passed argument supplier functions for formatted text message
		* @return Replaced source code
		*/
	def errorMessageWithLazyArguments(context: LoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(exception:Throwable)]] to
		* [[org.tinylog.Logger#error(exception:Throwable)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @return Replaced source code
		*/
	def errorException(context: LoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(exception.splice)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(exception:Throwable,message:String)]] to
		* [[org.tinylog.Logger#error(exception:Throwable,message:String)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed text message
		* @return Replaced source code
		*/
	def errorExceptionWithPlainMessage(context: LoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				org.tinylog.Logger.error(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				org.tinylog.Logger.error(exception.splice, message.splice)
			)
		}
	}

	/**
		* Redirects [[org.tinylog.scala.Logger#error(exception:Throwable,message:()=>String)]] to
		* [[org.tinylog.Logger#error(exception:Throwable,message:org.tinylog.Supplier)]].
		*
		* @param context
		* Macro context
		* @param exception
		* Passed exception or other throwable
		* @param message
		* Passed message supplier function
		* @return Replaced source code
		*/
	def errorExceptionWithLazyMessage(context: LoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(exception:Throwable,message:String,arguments:Any*)]] to
		* [[org.tinylog.Logger#error(exception:Throwable,message:String,arguments:Any*)]].
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
	def errorExceptionWithMessageWithPlainArguments(context: LoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]: _*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.Logger#error(exception:Throwable,message:String,arguments:()=>Any*)]] to
		* [[org.tinylog.Logger#error(exception:Throwable,message:String,arguments:org.tinylog.Supplier*)]].
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
	def errorExceptionWithMessageWithLazyArguments(context: LoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		org.tinylog.Logger.error(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}): _*
		)
	)

	/**
		* Converts a sequence of varargs into an expression.
		*
		* @param context
		* Macro context
		* @param seq
		* Sequence of varargs
		* @tparam T
		* Type of expressions
		* @return The passed sequence of varargs as expression
		*/
	private def convertSeqToExpr[T: context.WeakTypeTag](context: LoggerContext)
																											(seq: Seq[context.Expr[T]])
	: context.Expr[Seq[T]] = {
		seq.foldRight(context.universe.reify {
			Seq.empty[T]
		}) { (expr, acc) =>
			context.universe.reify {
				expr.splice +: acc.splice
			}
		}
	}

}
