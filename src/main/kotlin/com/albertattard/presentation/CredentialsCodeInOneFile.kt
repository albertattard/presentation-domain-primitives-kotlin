package com.albertattard.presentation

import java.util.concurrent.atomic.AtomicBoolean

// data class Credentials(val username: String, val password: String)
data class Credentials(val username: Username, val password: Password)

data class Username(val value: String)

// data class Password(val value: String) {
//
//    override fun toString() =
//        "--(masked password)--"
// }

class Password(value: String) {

    private val consumed = AtomicBoolean()

    val value: String = value
        get() =
            if (consumed.compareAndSet(false, true)) field
            else throw IllegalStateException("Password was already consumed")

    override fun toString() =
        "--(masked password)--"
}

// fun printCredentialsByMistake() {
//    val credentials = Credentials(
//        "username",
//        "a very secure long password that it is very hard to guess"
//    )
//    println("Logging into the system using: $credentials")
// }

fun main() {
// printCredentialsByMistake()
    val credentials = Credentials(
        Username("username"),
        Password("a very secure long password that it is very hard to guess")
    )
    println("Password: ${credentials.password.value}")
    println("Password: ${credentials.password.value}")
}
