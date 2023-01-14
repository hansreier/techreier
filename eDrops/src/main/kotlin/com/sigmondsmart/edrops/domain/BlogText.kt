package com.sigmondsmart.edrops.domain

import javax.persistence.*

@Entity
class BlogText(

    @Column
    var text: String,

    //Note: Bidirectional relationship one to one creates trouble with eager fetching
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    var blogEntry: BlogEntry,

    @Id
    val id: Long? = null
) {
    override fun toString() = "id: $id"
}