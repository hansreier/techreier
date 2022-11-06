package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime
import javax.persistence.*

//https://www.baeldung.com/kotlin/jpa
@Entity
class BlogEntry(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column
    val changed: LocalDateTime?,

    @Column
    val text: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null

)