package com.techreier.edrops.domain

data class Doc(val tag: String, val language: LanguageCode, val subject: String? = null,
               val ext: Boolean = true) {
    private companion object {
        var count = 0L
    }

    val id: Long = count

    init {
        count++
    }
}
