package com.sigmondsmart.edrops.domain

import jakarta.persistence.*
import java.time.LocalDateTime

//https://www.baeldung.com/kotlin/jpa
@Entity
class BlogEntry(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column
    var changed: LocalDateTime?,

    @Column
    var tag: String,

    @Column
    var version: Long,

    @Column
    var title: String,

    @Column(length = 400)
    var summary: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blogEntries", nullable = false)
    var blog: Blog,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null
) {
    override fun toString() = "id: $id blog: $blog created: $created changed: $changed text: $title"
}