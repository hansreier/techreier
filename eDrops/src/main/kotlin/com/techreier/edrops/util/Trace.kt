package com.techreier.edrops.util

// Tracing to be used for graph and calc (required not to use logback by Wasm)
enum class TraceLevel(val weight: Int) {
    OFF(0),
    ERROR(1),
    WARN(2),
    INFO(3),
    DEBUG(4),
    TRACE(5)
}

enum class TraceScope {
    DEFAULT, GRAPH, PARSE, CALC;

    // Låser alle tagger til nøyaktig 7 tegn med padding
    val tag: String get() = name.padEnd(7, ' ')
}

object Trace {
    var level: TraceLevel = TraceLevel.DEBUG

    // --- DEBUG ---
    fun debug(text: String? = "") = debug(TraceScope.DEFAULT, text)
    fun debug(scope: TraceScope, text: String? = "") {
        if (level.weight >= TraceLevel.DEBUG.weight) {
            println("[DEBUG] [${scope.tag}] $text")
        }
    }

    // --- TRACE ---
    fun trace(text: String? = "") = trace(TraceScope.DEFAULT, text)
    fun trace(scope: TraceScope, text: String? = "") {
        if (level.weight >= TraceLevel.TRACE.weight) {
            println("[TRACE] [${scope.tag}] $text")
        }
    }

    // --- INFO ---
    fun info(text: String? = "") = info(TraceScope.DEFAULT, text)
    fun info(scope: TraceScope, text: String? = "") {
        if (level.weight >= TraceLevel.INFO.weight) {
            println("[INFO ] [${scope.tag}] $text")
        }
    }

    // --- WARN ---
    fun warn(text: String? = "") = warn(TraceScope.DEFAULT, text)
    fun warn(scope: TraceScope, text: String? = "") {
        if (level.weight >= TraceLevel.WARN.weight) {
            println("[WARN ] [${scope.tag}] $text")
        }
    }

    // --- ERROR ---
    fun error(text: String? = "") = error(TraceScope.DEFAULT, text)
    fun error(scope: TraceScope, text: String? = "") {
        if (level.weight >= TraceLevel.ERROR.weight) {
            println("[ERROR] [${scope.tag}] $text")
        }
    }
}