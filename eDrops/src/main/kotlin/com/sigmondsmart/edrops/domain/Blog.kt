package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime
import javax.persistence.*

//https://www.baeldung.com/kotlin/jpa
@Entity
class Blog(

    @Column(nullable = false)
    val created: LocalDateTime,

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
    override fun toString() = "id: $id blogOwner: $blogOwner created: $created"
}