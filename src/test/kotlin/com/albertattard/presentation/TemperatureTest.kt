package com.albertattard.presentation

import kotlin.test.Test
import org.junit.Assert.assertEquals

class TemperatureTest {

    @Test
    fun `should convert from Fahrenheit to Celsius`() {
        val params = mapOf(
            Temperature.Fahrenheit(32.0) to Temperature.Celsius(
                0.0
            ),
            Temperature.Fahrenheit(72.0) to Temperature.Celsius(
                22.222
            )
        )
        params.forEach { (fahrenheit, celsius) ->
            assertEquals(celsius.value, fahrenheit.toCelsius().value, 0.001)
        }
    }

    @Test
    fun `should convert from Celsius to Fahrenheit`() {
        val params = mapOf(
            Temperature.Celsius(0.0) to Temperature.Fahrenheit(
                32.0
            ),
            Temperature.Celsius(22.222) to Temperature.Fahrenheit(
                72.0
            )
        )
        params.forEach { (celsius, fahrenheit) ->
            assertEquals(fahrenheit.value, celsius.toFahrenheit().value, 0.001)
        }
    }
}
