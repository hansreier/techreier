
package com.techreier.edrops.domain

import jakarta.persistence.*

@Entity
class LanguageCode(

    @Column
    var language: String,

    @Id
    val code: String
)