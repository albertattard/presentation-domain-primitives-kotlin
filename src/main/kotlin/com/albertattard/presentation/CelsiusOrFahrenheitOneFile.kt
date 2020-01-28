package com.albertattard.presentation

fun toCelsius(fahrenheit: Double) =
    (fahrenheit - 32) * 5.0 / 9.0

fun toFahrenheit(celsius: Double) =
    (celsius * 9.0 / 5.0) + 32

enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

sealed class Temperature {

    abstract fun toCelsius(): Celsius
    abstract fun toFahrenheit(): Fahrenheit

    data class Celsius(val value: Double) : Temperature() {
        override fun toCelsius() =
            this

        override fun toFahrenheit() =
            Fahrenheit((value * 9.0 / 5.0) + 32)
    }

    data class Fahrenheit(val value: Double) : Temperature() {
        override fun toCelsius() =
            Celsius((value - 32) * 5.0 / 9.0)

        override fun toFahrenheit() =
            this
    }
}
