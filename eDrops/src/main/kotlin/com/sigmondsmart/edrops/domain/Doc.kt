package com.sigmondsmart.edrops.domain

data class Doc(val tag: String, val name: String, val language: LanguageCode) {
    private companion object {
        var count = 0L
    }

    val id: Long = count

    init {
        count++
    }
}
