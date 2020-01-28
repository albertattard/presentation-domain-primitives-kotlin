package com.albertattard.presentation

import java.util.Timer
import java.util.TimerTask

fun main() {
    val task = object : TimerTask() {
        override fun run() {
            println("Running...")
        }
    }

    val timer = Timer()
    timer.scheduleAtFixedRate(task, 1000, 1000)
}

data class Delay(val value: Long)
data class InitialDelay(val value: Long)

class CronJob(function: () -> Unit) {

    fun runAtFixRate(initialDelay: InitialDelay, delay: Delay): CronJob {
        return this
    }
}
