package com.techreier.edrops.calc.utils

object Trace {

    fun info(text: String? = "") {
        println("[INFO ] $text")
    }

    fun warn(text: String? = "") {
        println("[WARN ] $text")
    }

    fun error(text: String? = "") {
        println("[ERROR] $text")
    }
}