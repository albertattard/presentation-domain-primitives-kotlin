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
## Small Steps Towards Better Software

---

class: impact

# Representation

---

# Order Number

An order number is a 10-digit number as shown next:

```
0980810031
```

The exception is for Stock, whose orders have a `-` as their second digit:

```
0-21200545
```

---

# Representation

How can we represent this value in our code?

---

# String Representation

We can represent the *Order Number* as a `String`:

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

However, while all *Order Number*s are `String`s, not all `String`s are *Order Number*s.

Only a very small subset of `String`s are *Order Number*s.

So the `String` data type is too generic.

There is something particular about *Order Number*s that we need to capture in our model.

---

# An Example

For example, let's say we pass a random `String` to a function that requires an *Order Number*:

```kotlin
val order = Order("any random string will do")
println("$order")
```

The above code will compile, even though the provided `String` is not an *Order Number*.

So the `String` data type is too generic. We need an alternative.

---

# A Possible Solution

We could add validation to ensure that the values passed are *Order Number*s.

Take for example this function for checking whether something is really an *Order Number*:

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

Then we could use `init` to check an argument before using it, and fail accordingly:

```kotlin
data class Order(val orderNumber: String) {
  init { OrderNumberValidation.check(orderNumber) }
}
```

We can use the same approach for all the other usages.

---

# Polluted Tests

A test needs to be added for every function that takes an *Order Number* as its input.

This will pollute the tests as we need to make sure that the inputs are properly checked:

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

We are polluting the tests because we are not taking advantage of the compiler.

For example, if we pass an `Int` to a function that expects a `String`, the compiler will complain:

```kotlin
fun aFunctionThatTakeAString(string: String) {}

aFunctionThatTakeAString(42)
```

We don't need to have tests for these cases as the compiler will handle them.

---

# Test Pyramid

.center[![Center-aligned image](assets/images/Test Pyramid.png)]

TODO: See alternatives for compilers for languages that do not use one, such as Php or Javascript

---

# Alternative Approach

Instead of using a *language primitive* to represent our data types, we can use a *domain primitive*:

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

# Is This a *Value Object*?

*Domain Primitives* are sometimes referred to as *Value Objects*, but there are some key differences.

*Value Objects* are usually used to represent types that are not available as a *language primitive*, such as `Money` or `Address`.

While similar, *Domain Primitives* additionally ensure that all instances are valid values of that type, and also that types are not reused, especially between contexts.

For example, the `Name` *Domain Primitive* cannot be used to represent a person's name and a computer's name at the same time. In such a case, we would have two *Domain Primitives*: `PersonName` and `ComputerName`.

---

# Use of Domain Primitives

Instead of `String`, we can now use `OrderNumber`:

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

# Take Advantage of the Compiler

Now we cannot create an `Order` with any random `String`, nor pass it to any function that requires an `OrderNumber`:

```kotlin
val order = Order("any random string will not do")
println("$order")
```

The above will not compile.

---

# Less Test Pollution

We now need only to make sure that only valid `OrderNumber`s can be created, and fail accordingly:

```kotlin
@Test 
fun `should throw an exception when given an invalid order number`() { 
  val invalidOrdersNumbers = 
        listOf("", "too long to be a valid order number") 
  invalidOrdersNumbers.forEach {
    assertFailsWith<IllegalArgumentException> { OrderNumber(it) }
  }
}
```

---

# Sealed Classes

Another approach would be to use sealed classes instead of throwing exceptions:

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

Sealed classes are the preferred option, as these streamline the usage:

```kotlin
when(OrderNumber("some random string")) {
  is OrderNumber.Invalid -> { /* Handle Invalid */ }
  is OrderNumber.Valid -> { /* Handle Valid */ }
}
```

(Note: This example did not include a `companion object` in the `OrderNumber` class due to slide size constraints.)

---

class: impact

# Ambiguity

---

# To Err is Human

Air Canada Flight 143 ran out of fuel on July 23, 1983, at an altitude of 41,000 feet (12,000 m), midway through the flight

The use of the incorrect conversion factor led to a total fuel load of only 22,300 pounds (10,100 kg) rather than the 22,300 kilograms that was needed

The crew was able to glide the Boeing 767 aircraft safely to an emergency landing

.center[![Center-aligned image](assets/images/Flight 143 after landing at Gimli Manitoba.png)]

---

# Bigger Than We Think

NASA’s Climate Orbiter was lost on September 23, 1999, due to metric/imperial mishap

.center[![Center-aligned image](assets/images/Climate Orbiter.png)]

---

# A Simple Example 

Our air-conditioner controller works with Celsius and has the following function, which is used to control the power of the compressor 

```kotlin
fun adjustPower(celsius: Double) {  }
```

Say that the temperature is `18°C`, but by mistake the Fahrenheit equivalent is given instead (`64.4°F`)

```kotlin
adjustPower(64.4) /* by mistake instead of 18 */
```

The controller will think that it's too hot and will put the air-conditioner to full power

---

# How can we Mitigate such Problems?

While we can easily convert between one temperature unit to another, we cannot tell in which unit the temperature is by just looking at the number

Can we use an `enum` to identify the unit, as shown next?

```kotlin
enum class TemperatureUnit {
    CELSIUS, FAHRENHEIT
}

fun adjustPower(temperature: Double, unit: TemperatureUnit) {  }
```

---

# Another Approach

Using `enum` will work, but we can do better

```kotlin
sealed class Temperature {

  abstract fun toCelsius(): Celsius
  abstract fun toFahrenheit(): Fahrenheit

  data class Celsius(val value: Double) : Temperature() { }

  data class Fahrenheit(val value: Double) : Temperature() { }
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

# Beyond Conversions

The ambiguity problem goes beyond simple conversions

Say you have a function that sets an order's delivery date as shown next

```kotlin
fun dispatchOrderOn(a: Int, b: Int, c: Int) { }
```

This function takes the day of the month, the month and the year as its parameters

By looking at the method signature, can you tell which is the month parameter and is it `0` based (that is, `0` is equivalent to January)?

---

class: impact

# Security

---

# Leaking Sensitive Information

How many times have we printed a password, or other sensitive information, by mistake?

```kotlin
data class Credentials(val username: String, val password: String)

val credentials = Credentials("username",
      "a very secure long password that it is very hard to guess")
println("Logging into the system using: $credentials")
```

The above example will print the very long and secure password

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

The above example will still print the password value

```
Password: a very secure long password that it is very hard to guess
```

---

# Can we Address this Somehow?

This is an area where domain primitives shine

Say that in our context, the password is only required to be read once, just to log into the system

If the password is read more than once, then we should fail as that's not the expected behaviour

Any unplanned reads will not go unnoticed

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

Having tight coupling between the application and the `java.util.Timer` may prove harder than expected

---

# Introducing An Abstraction Layer 

*Domain Primitives* can act as an abstraction layer between the *Language Primitives*, such as the `java.util.Timer` class, and the rest of the application

```kotlin
class CronJobTask {}

class CronJob {

    fun runAtFixRate(
          initialDelay: InitialDelay, 
          delay: Delay,
          block: () -> Any
        ): CronJobTask { }
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
