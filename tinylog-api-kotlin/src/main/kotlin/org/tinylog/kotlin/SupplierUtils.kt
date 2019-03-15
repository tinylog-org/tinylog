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

package org.tinylog.kotlin

import org.tinylog.Supplier

/**
 * Converts a function type into a supplier.
 *
 * @return Function type as supplier
 */
fun <T: Any?> (() -> T).asSupplier(): Supplier<T> = Supplier { invoke() }

/**
 * Converts an array of function types into an array of suppliers.
 *
 * @return Function types as suppliers
 */
fun <T: Any?> (Array<out () -> T>).asSuppliers(): Array<Supplier<T>> = map { it.asSupplier() }.toTypedArray()
