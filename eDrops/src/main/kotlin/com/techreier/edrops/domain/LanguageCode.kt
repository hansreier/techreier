
package com.techreier.edrops.domain

import jakarta.persistence.*

@Entity
class LanguageCode(

    @Column(nullable = false)
    var language: String,

    @Id
    val code: String

) {
    override fun toString() = "language: $language code: $code"
}