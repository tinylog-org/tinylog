package org.tinylog.kotlin

import java.util.function.Supplier

internal fun <T> (Array<out T>).withSuppliers(): Array<out Any?> {
    var target: Array<Any?>? = null

    for (i in indices) {
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
