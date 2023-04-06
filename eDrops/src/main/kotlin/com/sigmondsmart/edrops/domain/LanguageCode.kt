
package com.sigmondsmart.edrops.domain

import jakarta.persistence.*

@Entity
class LanguageCode(

    @Column
    var language: String,

    @Id
    val code: String
)