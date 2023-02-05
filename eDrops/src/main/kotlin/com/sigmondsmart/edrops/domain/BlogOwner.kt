package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class BlogOwner(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column
    var changed: LocalDateTime?,

    @Column
    var firstName: String,

    @Column
    var lastName: String,

    @Column
    var eMail: String,

    @Column
    var phones: String,

    @Column
    var address: String,

    @Column
    var zipCode: String,

    @Column
    var location: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = true)
  //  @OneToMany(mappedBy = "blogOwner", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OneToMany(mappedBy = "blogOwner", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("id DESC")
    var blogs: MutableSet<Blog>? = null
) {
    override fun toString() = "id: $id created: $created changed: $changed name: $firstName $lastName"
}