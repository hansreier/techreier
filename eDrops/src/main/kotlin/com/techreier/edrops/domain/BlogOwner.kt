package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_CODE_SIZE
import com.techreier.edrops.config.MAX_USERNAME_SIZE
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class BlogOwner(

    @Column(nullable = false, columnDefinition = "TIMESTAMP(0)")
    val created: LocalDateTime,

    @Column(columnDefinition = "TIMESTAMP(0)")
    var changed: LocalDateTime?,

    @Column(nullable = false, unique = true, length = MAX_USERNAME_SIZE)
    var username: String,

    @Column
    var password: String,

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

    @Column(nullable = false, length = MAX_CODE_SIZE)
    var zipCode: String,

    @Column(nullable = false)
    var location: String,

    @Column(nullable = false, length = MAX_CODE_SIZE)
    var countryCode: String,

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