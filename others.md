
# What Is What?

The `Measurements` data class comprise a list of `BigDecimal`s and the `slice()` function returns a new instance of the `Measurements` data class with a subset of its parent's class elements.

What values is the `slice()` function, shown next, expecting?

```kotlin
data class Measurements(private val elements: List<BigDecimal>) {

  fun slice(a: Int, b: Int): Measurements { }
}
```

---

# What Will We Get?

Is it the *start index* as the first parameter and *length* as the second?

Or is it the *start index* as the first parameter and the *end index* as the second parameter?

```kotlin
val original = Measurements( listOf(BigDecimal("0.01"), 
                 BigDecimal("0.34"), BigDecimal("2.67"), 
                 BigDecimal("1.002")) )
val slice = original.slice(1, 2)
```

Can we have both options?

---

# Use A `Range` Domain Primitive

Instead of using two language primitives, we can create a new domain primitive that defines the slice to be returned 

```kotlin
data class Measurements(private val elements: List<BigDecimal>) {

    fun slice(a: Range): Measurements { }
}
```

But how will this solve this problem?

---

# The `Range` Domain Primitive

```kotlin
data class Range private constructor(
    val start: StartIndex, val end: EndIndex) {
  companion object {
    @Throws(IllegalArgumentException::class)
    operator fun invoke(start: StartIndex, end: EndIndex): Range {
        require(start.value <= end.value) { "Invalid range" }
        return Range(start, end)
    }

    operator fun invoke(start: StartIndex, length: Length) =
      Range(start, start.endIndex(length))
  }
}
```

---

# The `StartIndex`

```kotlin
data class StartIndex private constructor(val value: Int) {
  companion object {
    @Throws(IllegalArgumentException::class)
    operator fun invoke(value: Int): StartIndex {
        require(value >= 0) { "Invalid start index" }
        return StartIndex(value)
    }
  }

  fun endIndex(length: Length) =
    EndIndex(value + length.value);
}
```

---

# The `EndIndex`

```kotlin
data class EndIndex private constructor(val value: Int) {
  companion object {
    @Throws(IllegalArgumentException::class)
    operator fun invoke(value: Int): EndIndex {
        require(value >= 0) { "Invalid end index" }
        return EndIndex(value)
    }
  }
}
```

---

# The `Length`

```kotlin
data class Length private constructor(val value: Int) {
  companion object {
    @Throws(IllegalArgumentException::class)
    operator fun invoke(value: Int): Length {
        require(value >= 0) { "Invalid length" }
        return Length(value)
    }
  }
}
```

---
