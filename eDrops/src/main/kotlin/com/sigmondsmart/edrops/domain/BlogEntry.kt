package com.sigmondsmart.edrops.domain

import java.time.LocalDateTime
import javax.persistence.*

//https://www.baeldung.com/kotlin/jpa
@Entity
class BlogEntry(

    @Column(nullable = false)
    val created: LocalDateTime,

    @Column
    var changed: LocalDateTime?,

    @Column
    var tag: String,

    @Column
    var version: Long,

    @Column
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blogs", nullable = false)
    var blog: Blog,

    @OneToOne(cascade = [CascadeType.ALL])
    @PrimaryKeyJoinColumn
    var blogText: BlogText?=null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null
) {
    override fun toString() = "id: $id blog: $blog created: $created changed: $changed text: $title"
}