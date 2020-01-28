package com.albertattard.presentation

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OrderCodeInOneFileKtTest {
    @Test
    fun `should return true when given a valid order number`() {
        val validOrdersNumbers = listOf("0-21200545", "0980810031")

        validOrdersNumbers.forEach {
            assertTrue(
                OrderNumberValidation.isValid(it),
                "The string '$it' is a valid order number"
            )
        }
    }

    @Test
    fun `should return false when given an invalid order number`() {
        val invalidOrdersNumbers = listOf("", "to long to be a valid order number")

        invalidOrdersNumbers.forEach {
            assertFalse(
                OrderNumberValidation.isValid(it),
                "The string '$it' is an invalid order number"
            )
        }
    }

    @Test
    fun `should throw an IllegalArgumentException when given an invalid order number`() {
        val invalidOrdersNumbers = listOf("", "to long to be a valid order number")
        invalidOrdersNumbers.forEach {
            assertFailsWith<IllegalArgumentException> {
                OrderNumber(it)
            }
        }
    }

//    @Test
//    fun `should throw an IllegalArgumentException when given an invalid order number`() {
//        val invalidOrdersNumbers = listOf("", "to long to be a valid order number")
//        invalidOrdersNumbers.forEach {
//            assertFailsWith<IllegalArgumentException> {
//                OrderNumberValidation.check(it)
//            }
//        }
//    }

//    @Test
//    fun `should throw an exception if passed an invalid order number`() {
//        assertFailsWith<IllegalArgumentException> {
//            Order("an invalid order number values")
//        }
//    }
}
