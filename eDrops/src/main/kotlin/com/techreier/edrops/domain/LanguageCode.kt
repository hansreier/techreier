
package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_CODE_SIZE
import jakarta.persistence.*

@Entity
open class LanguageCode(

    @Column(nullable = false)
    var language: String,

    @Id
    @Column(length = MAX_CODE_SIZE)
    val code: String

) {
    override fun toString() = "language=$language code=$code"
}