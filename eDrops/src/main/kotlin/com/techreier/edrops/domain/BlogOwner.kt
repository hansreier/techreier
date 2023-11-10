package com.techreier.edrops.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class BlogOwner(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column
    var changed: LocalDateTime?,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var eMail: String,

    @Column(nullable = false)
    var phones: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false)
    var zipCode: String,

    @Column(nullable = false)
    var location: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = true)
    @OneToMany(mappedBy = "blogOwner", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id DESC")
    var blogs: MutableSet<Blog>? = null
) {
    override fun toString() = "id: $id created: $created changed: $changed name: $firstName $lastName"
}