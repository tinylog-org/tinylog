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
	* Macros for transforming calls of [[org.tinylog.scala.TaggedLogger]] into calls of [[org.tinylog.TaggedLogger]].
	*/
private object TaggedLoggerMacro {
	
	private type TaggedLoggerContext = blackbox.Context { type PrefixType = TaggedLogger }

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isTraceEnabled]] to [[org.tinylog.TaggedLogger#isTraceEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isTraceEnabled(context: TaggedLoggerContext)
	                  ()
	: context.universe.Expr[Boolean] = context.universe.reify(
		context.prefix.splice.logger.isTraceEnabled
	)

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
	def tracePlainMessage(context: TaggedLoggerContext)
	                     (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.trace(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.trace(message.splice.asInstanceOf[Any])
			)
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
	def traceLazyMessage(context: TaggedLoggerContext)
	                    (message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

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
	def traceMessageWithPlainArguments(context: TaggedLoggerContext)
	                                  (message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def traceMessageWithLazyArguments(context: TaggedLoggerContext)
	                                 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

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
	def traceException(context: TaggedLoggerContext)
	                  (exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(exception.splice)
	)

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
	def traceExceptionWithPlainMessage(context: TaggedLoggerContext)
	                                  (exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.trace(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.trace(exception.splice, message.splice)
			)
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
	def traceExceptionWithLazyMessage(context: TaggedLoggerContext)
	                                 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

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
	def traceExceptionWithMessageWithPlainArguments(context: TaggedLoggerContext)
	                                               (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def traceExceptionWithMessageWithLazyArguments(context: TaggedLoggerContext)
	                                              (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.trace(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isDebugEnabled]] to [[org.tinylog.TaggedLogger#isDebugEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isDebugEnabled(context: TaggedLoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		context.prefix.splice.logger.isDebugEnabled
	)

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
	def debugPlainMessage(context: TaggedLoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.debug(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.debug(message.splice.asInstanceOf[Any])
			)
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
	def debugLazyMessage(context: TaggedLoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

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
	def debugMessageWithPlainArguments(context: TaggedLoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def debugMessageWithLazyArguments(context: TaggedLoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

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
	def debugException(context: TaggedLoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(exception.splice)
	)

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
	def debugExceptionWithPlainMessage(context: TaggedLoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.debug(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.debug(exception.splice, message.splice)
			)
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
	def debugExceptionWithLazyMessage(context: TaggedLoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

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
	def debugExceptionWithMessageWithPlainArguments(context: TaggedLoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def debugExceptionWithMessageWithLazyArguments(context: TaggedLoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.debug(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isInfoEnabled]] to [[org.tinylog.TaggedLogger#isInfoEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isInfoEnabled(context: TaggedLoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		context.prefix.splice.logger.isInfoEnabled
	)

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
	def infoPlainMessage(context: TaggedLoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.info(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.info(message.splice.asInstanceOf[Any])
			)
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
	def infoLazyMessage(context: TaggedLoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

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
	def infoMessageWithPlainArguments(context: TaggedLoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def infoMessageWithLazyArguments(context: TaggedLoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

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
	def infoException(context: TaggedLoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(exception.splice)
	)

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
	def infoExceptionWithPlainMessage(context: TaggedLoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.info(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.info(exception.splice, message.splice)
			)
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
	def infoExceptionWithLazyMessage(context: TaggedLoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

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
	def infoExceptionWithMessageWithPlainArguments(context: TaggedLoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def infoExceptionWithMessageWithLazyArguments(context: TaggedLoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.info(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isWarnEnabled]] to [[org.tinylog.TaggedLogger#isWarnEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isWarnEnabled(context: TaggedLoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		context.prefix.splice.logger.isWarnEnabled
	)

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
	def warnPlainMessage(context: TaggedLoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.warn(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.warn(message.splice.asInstanceOf[Any])
			)
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
	def warnLazyMessage(context: TaggedLoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

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
	def warnMessageWithPlainArguments(context: TaggedLoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def warnMessageWithLazyArguments(context: TaggedLoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

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
	def warnException(context: TaggedLoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(exception.splice)
	)

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
	def warnExceptionWithPlainMessage(context: TaggedLoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.warn(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.warn(exception.splice, message.splice)
			)
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
	def warnExceptionWithLazyMessage(context: TaggedLoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

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
	def warnExceptionWithMessageWithPlainArguments(context: TaggedLoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def warnExceptionWithMessageWithLazyArguments(context: TaggedLoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.warn(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

	/**
		* Redirects [[org.tinylog.scala.TaggedLogger#isErrorEnabled]] to [[org.tinylog.TaggedLogger#isErrorEnabled]].
		*
		* @param context
		* Macro context
		* @return Replaced source code
		*/
	def isErrorEnabled(context: TaggedLoggerContext)
										()
	: context.universe.Expr[Boolean] = context.universe.reify(
		context.prefix.splice.logger.isErrorEnabled
	)

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
	def errorPlainMessage(context: TaggedLoggerContext)
											 (message: context.Expr[Any])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.error(new Supplier[Any] {
					override def get(): Any = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.error(message.splice.asInstanceOf[Any])
			)
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
	def errorLazyMessage(context: TaggedLoggerContext)
											(message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(new Supplier[Any] {
			override def get(): Any = message.splice.apply()
		})
	)

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
	def errorMessageWithPlainArguments(context: TaggedLoggerContext)
																		(message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def errorMessageWithLazyArguments(context: TaggedLoggerContext)
																	 (message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
		)
	)

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
	def errorException(context: TaggedLoggerContext)
										(exception: context.Expr[Throwable])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(exception.splice)
	)

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
	def errorExceptionWithPlainMessage(context: TaggedLoggerContext)
																		(exception: context.Expr[Throwable], message: context.Expr[String])
	: context.universe.Expr[Unit] = {
		if (message.actualType =:= context.universe.typeOf[String] && message.tree.children.nonEmpty) {
			context.universe.reify(
				context.prefix.splice.logger.error(exception.splice, new Supplier[String] {
					override def get(): String = message.splice
				})
			)
		} else {
			context.universe.reify(
				context.prefix.splice.logger.error(exception.splice, message.splice)
			)
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
	def errorExceptionWithLazyMessage(context: TaggedLoggerContext)
																	 (exception: context.Expr[Throwable], message: context.Expr[() => String])
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(exception.splice, new Supplier[String] {
			override def get(): String = message.splice.apply()
		})
	)

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
	def errorExceptionWithMessageWithPlainArguments(context: TaggedLoggerContext)
																								 (exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.asInstanceOf[Seq[Object]]:_*
		)
	)

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
	def errorExceptionWithMessageWithLazyArguments(context: TaggedLoggerContext)
																								(exception: context.Expr[Throwable], message: context.Expr[String], arguments: context.Expr[() => Any]*)
	: context.universe.Expr[Unit] = context.universe.reify(
		context.prefix.splice.logger.error(
			exception.splice,
			message.splice,
			convertSeqToExpr(context)(arguments).splice.map(argument => new Supplier[Any] {
				override def get(): Any = argument.apply()
			}):_*
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
	private def convertSeqToExpr[T: context.WeakTypeTag](context: TaggedLoggerContext)
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
