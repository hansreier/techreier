package com.sigmondsmart.edrops.domain

import javax.persistence.*

@Entity
class BlogText(

    @Column
    var text: String,

    @OneToOne
    @MapsId
    @JoinColumn(name = "blogEntry", nullable = false)
    var blogEntry: BlogEntry,

    @Id
    @Column
    val id: Long
) {
    override fun toString() = "id: $id"
}