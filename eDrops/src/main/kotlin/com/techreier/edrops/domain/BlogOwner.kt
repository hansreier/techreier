package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_CODE_SIZE
import com.techreier.edrops.config.MAX_USERNAME_SIZE
import jakarta.persistence.*
import java.time.Instant

@Entity
class BlogOwner(

    @Column(nullable = false, columnDefinition ="datetime(0)")
    val created: Instant,

    @Column(columnDefinition ="datetime(0)")
    var changed: Instant,

    @Column(nullable = false, unique = true, length = MAX_USERNAME_SIZE)
    var username: String,

    @Column
    var password: String,

    @Column
    var accessLevel: Int,

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(nullable = false)
    var email: String,

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

    @OneToMany(mappedBy = "blogOwner", cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id DESC")
    var blogs: MutableSet<Blog>,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

) {

    fun copyAttributes(other: BlogOwner): BlogOwner {
        this.changed = other.changed
        this.username = other.username
        this.password = other.password
        this.accessLevel = other.accessLevel
        this.email = other.email
        this.phones = other.phones
        this.address = other.address
        this.zipCode = other.zipCode
        this.location = other.location
        this.countryCode = other.countryCode
        return this
    }
    override fun toString() = "id=$id username=$username name=$firstName $lastName accessLevel=$accessLevel"
}