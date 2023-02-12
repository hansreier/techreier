package com.sigmondsmart.edrops.domain

import javax.persistence.*

@Entity
class BlogText(

    @Column
    var text: String,

    // Note: Bidirectional relationship one to one creates trouble with eager fetching
    // https://stackoverflow.com/questions/1444227/how-can-i-make-a-jpa-onetoone-relation-lazy
    // And not mandatory BlogTexts create other problems...
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    var blogEntry: BlogEntry,

    @Id
    val id: Long? = null
) {
    override fun toString() = "id: $id"
}