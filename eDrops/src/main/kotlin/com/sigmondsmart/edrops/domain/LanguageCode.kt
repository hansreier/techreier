package com.sigmondsmart.edrops.domain

import javax.persistence.*

@Entity
class LanguageCode(

    @Column
    var language: String,

    @Id
    val code: String
)