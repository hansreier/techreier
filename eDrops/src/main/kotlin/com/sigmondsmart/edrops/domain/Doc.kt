package com.sigmondsmart.edrops.domain

data class Doc(val tag: String, val name: String, val language: LanguageCode) {
    var id: Long = Docs.count

    init {
        Docs.count++
    }
}
