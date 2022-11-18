package org.tinylog.kotlin

import java.util.function.Supplier

internal fun <T> (() -> T).asSupplier(): Supplier<T> {
    return Supplier { this.invoke() }
}

internal fun <T> (Array<out T>).withSuppliers(): Array<out Any?> {
    var target: Array<Any?>? = null

    for (i in 0 until this.size) {
        val element = this[i]
        if (element is Function0<*>) {
            if (target == null) {
                target = arrayOf(this)
            }

            target[i] = Supplier { element.invoke() }
        }
    }

    return target ?: this
}
