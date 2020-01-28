title: Domain Primitives
class: animation-fade
layout: true

<!-- This slide will serve as the base layout for all your slides -->
.bottom-bar[
  {{title}}
]

---

class: impact

# {{title}}
## Building Better Software

---

class: impact

# Data Types

---

# Order Number

An order number is a 10 digit number as shown next

```
0980810031
```

With an exception for stock.  Stock orders have a `-` as their second digit

```
0-21200545
```
---

# Representation

How can we represent this value in our code?

---

# String Representation

We can represent the *Order Number* as `String`

```kotlin
data class Order(val orderNumber: String)
```

```kotlin
object OrderGateway {
  fun fetch(orderNumber: String) : Order { }
}
```

```kotlin
object OrderRepository {
  fun delete(orderNumber: String) { }
}
```

---

# Challenges

While all *Order Number*s are `String`s, not all `String`s are *Order Number*s

A very small subset of `String`s are *Order Number*s

The `String` data type is too generic

---

# All `String`s Will Do

We can pass any `String` to any function that requires an *Order Number*

```kotlin
val order = Order("any random string will do")
println("$order")
```

The above code will compile, even though the provided `String` is not an *Order Number*

---

# A Possible Solution

We can add validation to ensure that the values passed are *Order Number*s 

```kotlin
object OrderNumberValidation {
  fun isValid(orderNumber: String): Boolean { }

  @Throws(IllegalArgumentException::class)
  fun check(orderNumber: String): String {
    require (isValid(orderNumber)) { "Invalid order number" }
    return orderNumber
  }
}
```

---

# Use Validation

We can check the arguments before using them, and fail accordingly

```kotlin
data class Order(val orderNumber: String) {
  init {
    OrderNumberValidation.check(orderNumber)
  }
}
```

We can use the same approach for all the other usage

---

# Polluted Tests

A test needs to be added to every function that takes an *Order Number* as its input.  

This will pollute the tests as we need to make sure that the inputs are properly checked

```kotlin
@Test
fun `should throw an exception if passed an invalid order number`() {
    assertFailsWith<IllegalArgumentException> {
        Order("a random string that it is not an Order Number")
    }
}
```

---

# The Forgotten Compiler

We are polluting the tests as we are not taking advantage of the compiler

For example, if we pass an `Int` to a function that expects a `String`, the compiler will complain

```kotlin
fun aFunctionThatTakeAString(string: String) {}

aFunctionThatTakeAString(42)
```

We don't need to have a tests for these cases as the compiler will handle them

---

# Test Pyramid

.center[![Center-aligned image](assets/images/Test Pyramid.png)]

---

# Alternative Approach

Instead of using a language primitive to represent data types we can use domain primitives

```kotlin
data class OrderNumber private constructor(val value: String) {
    companion object {
        @Throws(IllegalArgumentException::class)
        operator fun invoke(value: String): OrderNumber {
            require(value.length == 10) { "Invalid order number" }
            return OrderNumber(value)
        }
    }
}
```

---

# Is This A *Value Object*?

*Domain Primitives* are sometimes referred to as *Value Objects*, but there are some key differences

*Value Objects* are usually used to represent types that are not available as a *Language Primitive*, such as `Money` or `Address`

While similar, *Domain Primitives* ensure that all instances are valid values of that type and types are not reused, especially between context

For example, the `Name` *Domain Primitive* cannot be used to represent persons' name and computers' name at the same time.  We will have two *Domain Primitives* in such case, `PersonName` and `ComputerName`

---

# Use Of Domain Primitives

Instead of `String`s, we can now use `OrderNumber`

```kotlin
data class Order(val orderNumber: OrderNumber)
```

```kotlin
object OrderGateway {
  fun fetch(orderNumber: OrderNumber) : Order { }
}
```

```kotlin
object OrderRepository {
  fun delete(orderNumber: OrderNumber) { }
}
```

---

# Take Advantage Of Compiler

We cannot create an `Order` with any random `String`, nor pass it to any function that requires an `OrderNumber`

```kotlin
val order = Order("any random string will not do")
println("$order")
```

The above will not compile

---

# Less Test Pollution

We only need to make sure that only valid `OrderNumber`s can be created, and fail accordingly

```kotlin
@Test
fun `should throw an exception when given an invalid order number`() {
  val invalidOrdersNumbers = 
          listOf("", "to long to be a valid order number")
  invalidOrdersNumbers.forEach {
    assertFailsWith<IllegalArgumentException> { OrderNumber(it) }
  }
}
```

---

# Sealed Classes

Another approach is to use seal classes instead of throwing exceptions

```kotlin
sealed class OrderNumber {

  object Invalid : OrderNumber()

  data class Valid private constructor(val value: String) : OrderNumber() {
    companion object {
      operator fun invoke(value: String): OrderNumber {
        return if(value.length != 10) Invalid
               else Valid(value)
      }
    }
  }
}
```

---

# Streamlined Usage

Sealed Classes are a preferred option as these streamline the usage

```kotlin
when(OrderNumber("some random string")) {
  is OrderNumber.Invalid -> { /* Handle Invalid */ }
  is OrderNumber.Valid -> { /* Handle Valid */ }
}
```

This example did not include a `companion object` in the `OrderNumber` class due to slides size constraints

---

class: impact

# Ambiguity

---

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

# Celsius Or Fahrenheit 

Converting between Celsius to Fahrenheit, and vice versa, is a typical example found in many programming textbooks.

```kotlin
fun toCelsius(fahrenheit: Double) =
    (fahrenheit - 32.0) * 5.0 / 9.0

fun toFahrenheit(celsius: Double) =
    (celsius * 9.0 / 5.0) + 32.0
```

As shown above, the conversion is quite simple and straightforward

---

# Controller 

Our air-conditioner controller works with Celsius and has the following function, which is used to control the power of the compressor 

```kotlin
fun adjustPower(celsius: Double) {  }
```

Say that the temperature is `18°C`, but by mistake the Fahrenheit equivalent is given instead (`64.4°F`)

The controller will think that it's too hot and will put the compressors to full power

---

# How Can We Address This? 

While we can easily convert between one temperature unit to another, we cannot tell in which unit the temperature is

Can we use an `enum` to identify the unit, as shown next?

```kotlin
enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

fun adjustPower(celsius: Double, unit: TemperatureUnit) {  }
```

---

# Domain Primitives? 

Using `enum` will work, but we can do better

```kotlin
sealed class Temperature {

  abstract fun toCelsius(): Celsius
  abstract fun toFahrenheit(): Fahrenheit

  data class Celsius(val celsius: Double) : Temperature() { }

  data class Fahrenheit(val fahrenheit: Double) : Temperature() { }
}
```

---

# How Does This Works? 

```kotlin
data class Celsius(val value: Double) : Temperature() {
  override fun toCelsius() =
    this

  override fun toFahrenheit() =
    Fahrenheit((value * 9.0 / 5.0) + 32.0)
}
```

---

# Improved Controller 

The controller can now take any type of temperature unit and can safely convert it to the required type

```kotlin
fun adjustPower(temperature: Temperature) {  
  val celsius = temperature.toCelsius()
  /* Work with the proper temperature */
}
```

This will ensure that the controller always work with Celsius, irrespective of the temperature unit provided

---

# Bigger Than We Think

NASA’s Climate Orbiter was lost September 23, 1999 due to metric/imperial mishap ([http://edition.cnn.com/TECH/space/9909/30/mars.metric/](http://edition.cnn.com/TECH/space/9909/30/mars.metric/))

.center[![Center-aligned image](assets/images/Climate Orbiter.png)]

---

class: impact

# Security

---

# Password

How many times have we printed a password, or other sensitive information, by mistake?

```kotlin
data class Credentials(val username: String, val password: String)

val credentials = Credentials("username",
      "a very secure long password that it is very hard to guess")
println("Logging into the system using: $credentials")
```

Will print the very long and secure password

```
... password=a very secure long password that it is very hard to guess
```

---

# How Can We Prevent That?

We can prevent the password from being printed by using a domain primitive and overriding the `toString()` function

```kotlin
data class Password(val value: String) {

  override fun toString() =
    "--(masked password)--"
}
```

---

# But What About …

We can still print the password by getting its value

```kotlin
val credentials = Credentials(
  Username("username"),
  Password("a very secure long password that it is very hard to guess")
)
println("Password: ${credentials.password.value}")
```

The above example will print the password value

```
Password: a very secure long password that it is very hard to guess
```

---

# Limit The Number of Reads 

This is an area where domain primitives shine

Say that in our context, the password is only required to be read once, just to log into the system

If the password is read more than once, then we should fail and unplanned reads will not go unnoticed

---

# How Can We Do That? 

```kotlin
class Password(value: String) {

  private val consumed = AtomicBoolean()

  val value: String = value
      get() =
          if (consumed.compareAndSet(false, true)) field
          else throw IllegalStateException(
                         "Password was already consumed")

  override fun toString() =
      "--(masked password)--"
}
```

---

# How Does This Work? 

```kotlin
val credentials = Credentials(Username("a"), Password("b"))

/* The first time will work */
println("First try: ${credentials.password.value}")

/* The second time will throw an exception */
println("Second try: ${credentials.password.value}")
```

Any unexpected reads will not go unnoticed

```
java.lang.IllegalStateException: Password was already consumed
```

---

# Is This A Silver Bullet? 

No!!

We can always store the password in a language primitive, such as `String`, and then print this variable as many times we want to 

```kotlin
val credentials = Credentials(Username("a"), Password("b"))
val password = credentials.password.value

/* Now we can print the password as many times we want */
repeat(100) {
  println("Password: $password")
}
```

---

class: impact

# Refactoring

---

# Coupling With Language Primitives 

Say that we need to schedule some tasks to run every so ofter, like a cron job

One way to do this is to use the `java.util.Timer` and `java.util.TimerTask` classes as shown next

```kotlin
val task = object : TimerTask() {
  override fun run() {
    println("Running...")
  }
}

val timer = Timer()
timer.scheduleAtFixedRate(task, 1000, 1000)
```

---

# What's Wrong With That? 

Without an abstraction layer between the language primitives types and the application, swapping the `java.util.Timer` class could be harder than expected

For example, the `java.util.Timer`, will stop running if the `java.util.TimerTask` throws a `RuntimeException`

That's may be unexpected behaviour and you would like to swap the `java.util.Timer` class to the `java.util.concurrent.ScheduledExecutorService` class

Having tight coupling between the application and the `java.util.Timer` would prove such take harder than expected


---

# Introducing An Abstraction Layer 

*Domain Primitives* can act as an abstraction layer between the *Language Primitives*, such as the `java.util.Timer` class, and the rest of the application

```kotlin
class CronJob(function: () -> Unit) {

    fun runAtFixRate(initialDelay: InitialDelay, delay: Delay): CronJob { }
}
```

Swapping the internals of the `CronJob` class should not effect any other part of the application

---

class: impact

# Shortcomings

---

# Verbosity

Domain Primitives may reduce ambiguity and improves security, but at the expense of verbosity

```kotlin
original.slice(1, 2)
original.slice(Range(StartIndex(1), Length(2)))
```

---

# Compatibility

Language primitives are compatible to other libraries, while domain primitives are not and need to be converted back and forth

---

# Misuse

The same domain primitive, cannot be reused as each domain primitive should serve one purpose

For example, *name* and *surname* should be represented by two domain primitives
---

class: impact

# Thank You
## Feedback makes us better
