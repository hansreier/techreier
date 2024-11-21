package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*
import java.time.LocalDateTime

//https://www.baeldung.com/kotlin/jpa
@Entity
class Blog(

    @Column(nullable = false, columnDefinition = "TIMESTAMP(0)")
    val changed: LocalDateTime,

    @Column(nullable = false, length = MAX_SEGMENT_SIZE)
    var segment: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "code", nullable = false)
    var language: LanguageCode,

    @Column(nullable = false, length = MAX_TITLE_SIZE)
    var subject: String,

    @Column(nullable = false, length = MAX_SUMMARY_SIZE)
    var about: String,

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
    override fun toString() = "id: $id segment: $segment subject: $subject language: ${language.language} blogOwner: $blogOwner changed: $changed"
}