package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime
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
    @OneToMany(cascade = [CascadeType.ALL])
    val blogEntries: List<BlogEntry>? = null

) {
    override fun toString() = "id: $id created: $created changed: $changed name: $firstName $lastName"
}