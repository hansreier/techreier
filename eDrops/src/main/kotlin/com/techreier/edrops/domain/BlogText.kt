
package com.techreier.edrops.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
class BlogText(

    @Column(columnDefinition ="timestamp(0)")
    var changed: Instant?,

    @Column(nullable = false, columnDefinition ="TEXT")
    var text: String,

    // Note: Bidirectional relationship one to one creates trouble with eager fetching
    // https://stackoverflow.com/questions/1444227/how-can-i-make-a-jpa-onetoone-relation-lazy
    // https://vladmihalcea.com/the-best-way-to-map-a-onetoone-relationship-with-jpa-and-hibernate/
    // And not mandatory BlogTexts create other problems...
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    var blogPost: BlogPost,

    @Id
    val id: Long? = null
) {
    override fun toString() = "id: $id"
}