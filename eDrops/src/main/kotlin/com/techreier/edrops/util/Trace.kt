package com.techreier.edrops.util

import com.techreier.edrops.config.traceLevel

// Tracing to be used for graph and calc (required not to use logback by Wasm)
enum class TraceLevel(val weight: Int) {
    OFF(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4)
}

object Trace {

    fun debug(text: String? = "") {
        if (traceLevel.weight >= TraceLevel.DEBUG.weight) {
            println("[DEBUG] $text")
        }
    }

    fun info(text: String? = "") {
        if (traceLevel.weight >= TraceLevel.INFO.weight) {
            println("[INFO ] $text")
        }
    }

    fun warn(text: String? = "") {
        if (traceLevel.weight >= TraceLevel.WARN.weight) {
            println("[WARN ] $text")
        }
    }

    fun error(text: String? = "") {
        if (traceLevel.weight >= TraceLevel.ERROR.weight) {
            println("[ERROR] $text")
        }
    }
}
