package com.techreier.edrops.domain

import jakarta.persistence.*
import java.time.LocalDateTime

//https://www.baeldung.com/kotlin/jpa
@Entity
class Blog(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column(nullable = false)
    var tag: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "code", nullable = false)
    var language: LanguageCode,

    @Column(nullable = false)
    var subject: String,

    @Column(nullable = true)
    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var blogEntries: MutableList<BlogEntry>? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blogs", nullable = false)
    var blogOwner: BlogOwner,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null,
) {
    override fun toString() = "id: $id tag: $tag subject: $subject language: ${language.language} blogOwner: $blogOwner created: $created"
}