package com.albertattard.presentation

import java.math.BigDecimal

data class Measurements(private val elements: List<BigDecimal>) {

    fun slice(a: Range): Measurements {
        TODO("Will implement this later on")
    }
}

fun usingSlice() {
    val original = Measurements(
        listOf(
            BigDecimal("12.34"),
            BigDecimal("5.67"),
            BigDecimal("8.9")
        )
    )
    original.slice(
        Range(
            StartIndex(1),
            Length(2)
        )
    )
}

data class StartIndex private constructor(val value: Int) {
    companion object {
        @Throws(IllegalArgumentException::class)
        operator fun invoke(value: Int): StartIndex {
            require(value >= 0) { "Invalid start index" }
            return StartIndex(value)
        }
    }

    fun endIndex(length: Length) =
        EndIndex(value + length.value)
}

data class EndIndex private constructor(val value: Int) {
    companion object {
        @Throws(IllegalArgumentException::class)
        operator fun invoke(value: Int): EndIndex {
            require(value >= 0) { "Invalid end index" }
            return EndIndex(value)
        }
    }
}

data class Length private constructor(val value: Int) {
    companion object {
        @Throws(IllegalArgumentException::class)
        operator fun invoke(value: Int): Length {
            require(value >= 0) { "Invalid length" }
            return Length(value)
        }
    }
}

data class Range private constructor(val start: StartIndex, val end: EndIndex) {
    companion object {
        @Throws(IllegalArgumentException::class)
        operator fun invoke(start: StartIndex, end: EndIndex): Range {
            require(start.value <= end.value) { "Invalid range" }
            return Range(start, end)
        }

        operator fun invoke(start: StartIndex, length: Length): Range {
            return Range(start, start.endIndex(length))
        }
    }
}
