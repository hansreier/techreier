package com.techreier.edrops.util

import kotlin.time.TimeMark
import kotlin.time.TimeSource

// Tracing to be used for graph and calc (required not to use logback by Wasm)
enum class TraceLevel(val weight: Int) {
    OFF(0),
    ERR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    TRACE(5)
}

enum class Scope {
    DEFAULT, GRAPH, PARSE, CALC;
    val tag: String get() = name.padEnd(7, ' ')
}

object Trace {
    var level: TraceLevel = TraceLevel.INFO
    var timestamp: Boolean = false
    private var startMark: TimeMark = TimeSource.Monotonic.markNow()

    fun resetTimer() {
        startMark = TimeSource.Monotonic.markNow()
    }

    private fun log(msgLevel: TraceLevel, label: String, scope: Scope, text: String?) {
        if (level.weight >= msgLevel.weight) {
            val timePrefix = if (timestamp) {
                val ms= startMark.elapsedNow().inWholeMicroseconds
                "[${formatElapsed(ms)}] "
            } else {
                ""
            }
            println("$timePrefix[$label] [${scope.tag}] $text")
        }
    }

    private fun formatElapsed(micros: Long): String {
        val wholeMs = micros / 1000
        val frac = (micros % 1000).toString().padStart(3, '0')
        return "${wholeMs.toString().padStart(4)}.$frac"
    }

    fun debug(text: String? = "") = debug(Scope.DEFAULT, text)
    fun debug(scope: Scope, text: String? = "") = log(TraceLevel.DEBUG, "DEBUG", scope, text)

    fun trace(text: String? = "") = trace(Scope.DEFAULT, text)
    fun trace(scope: Scope, text: String? = "") = log(TraceLevel.TRACE, "TRACE", scope, text)

    fun info(text: String? = "") = info(Scope.DEFAULT, text)
    fun info(scope: Scope, text: String? = "") = log(TraceLevel.INFO, "INFO ", scope, text)

    fun warn(text: String? = "") = warn(Scope.DEFAULT, text)
    fun warn(scope: Scope, text: String? = "") = log(TraceLevel.WARN, "WARN ", scope, text)

    fun err(text: String? = "") = err(Scope.DEFAULT, text)
    fun err(scope: Scope, text: String? = "") = log(TraceLevel.ERR, "ERR", scope, text)
}