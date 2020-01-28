package com.albertattard.presentation

data class OrderNumber private constructor(val value: String) {
    companion object {
        @Throws(IllegalArgumentException::class)
        operator fun invoke(value: String): OrderNumber {
            require(value.length == 10) { "Invalid order number" }
            return OrderNumber(value)
        }
    }
}

// sealed class OrderNumber {
//
//    object Invalid : OrderNumber()
//
//    data class Valid private constructor(val value: String) : OrderNumber() {
//        companion object {
//            @Throws(IllegalArgumentException::class)
//            operator fun invoke(value: String): OrderNumber {
//                if(value.length != 10) Invalid
//                return Valid(value)
//            }
//        }
//    }
// }

data class Order(val orderNumber: OrderNumber)

object OrderGateway {
    fun fetch(orderNumber: OrderNumber): Order {
        TODO("Will fetch it later on")
    }
}

object OrderNumberValidation {
    fun isValid(orderNumber: String): Boolean {
        if (orderNumber.length != 10)
            return false

        return true
    }

    @Throws(IllegalArgumentException::class)
    fun check(orderNumber: String): String =
        if (isValid(orderNumber)) orderNumber
        else throw IllegalArgumentException("Invalid order number")
}

// fun passAnIntInsteadOfAString() {
//    val order = Order(0123456789)
// }

// fun passAnInvalidOrderNumber(){
//    val order = Order("an invalid order number value")
//
//    val fetched = OrderGateway.fetch("a random string")
// }

fun main() {
}
