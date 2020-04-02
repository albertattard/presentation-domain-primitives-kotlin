package com.albertattard.presentation

import java.util.concurrent.atomic.AtomicBoolean

// data class CreditCardNumber(val value: Long) {
class CreditCardNumber(value: Long) {

    private val maskedValue = "xxxx-xxxx-xxxx-${value % 10000}"

    private val consumed = AtomicBoolean()

    val value: Long = value
        get() =
            if (consumed.compareAndSet(false, true)) field
            else throw IllegalStateException(
                "Credit card number was already consumed"
            )

    override fun toString() =
        maskedValue
}

fun main() {
    val number = CreditCardNumber(5555_5555_5555_5555)
    println("First try: ${number.value}")
    println("Second try: ${number.value}")
}

