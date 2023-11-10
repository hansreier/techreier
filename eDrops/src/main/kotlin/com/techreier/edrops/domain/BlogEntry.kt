package com.techreier.edrops.domain

import jakarta.persistence.*
import java.time.LocalDateTime

//https://www.baeldung.com/kotlin/jpa
@Entity
class BlogEntry(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column
    var changed: LocalDateTime?,

    @Column(nullable = false)
    var tag: String,

    @Column(nullable = false)
    var version: Long,

    @Column(nullable = false)
    var title: String,

    // @Column(length = 400) TODO revert
    @Column(length = 58000, nullable = false)
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