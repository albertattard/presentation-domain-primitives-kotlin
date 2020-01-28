package com.albertattard.presentation

import kotlin.test.Test
import org.junit.Assert.assertEquals

class CelsiusOrFahrenheitOneFileKtTest {

    @Test
    fun `should convert from Fahrenheit to Celsius`() {
        val params = mapOf(32.0 to 0.0,
            72.0 to 22.222)
        params.forEach { (fahrenheit, celsius) ->
            assertEquals(celsius, toCelsius(fahrenheit), 0.001)
        }
    }

    @Test
    fun `should convert from Celsius to Fahrenheit`() {
        val params = mapOf(0.0 to 32.0,
            22.222 to 72.0)
        params.forEach { (celsius, fahrenheit) ->
            assertEquals(fahrenheit, toFahrenheit(celsius), 0.001)
        }
    }
}
