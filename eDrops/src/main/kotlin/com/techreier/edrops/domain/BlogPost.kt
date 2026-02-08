package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_STATE_SIZE
import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*
import java.time.Instant

//https://www.baeldung.com/kotlin/jpa
@Entity
class BlogPost(

    @Column(nullable = false, columnDefinition = "datetime(0)")
    var changed: Instant,

    @Column(nullable = false, length = MAX_STATE_SIZE)
    var state: String,

    @Column(nullable = false, length = MAX_SEGMENT_SIZE)
    var segment: String,

    @Column(nullable = false, length = MAX_TITLE_SIZE)
    var title: String,

    @Column(nullable = false, length = MAX_SUMMARY_SIZE)
    var summary: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    var blog: Blog,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null,

    @Column( columnDefinition = "datetime(0)", nullable = false, updatable = false)
    var created: Instant = Instant.now(),
) {

    constructor(
        timestamp: Instant,
        segment: String,
        title: String,
        summary: String,
        blog: Blog
    ) : this(timestamp, segment, title, summary, blog, null) {
        this.created = timestamp
    }

    fun copyAttributes(other: BlogPost): BlogPost {
        this.changed = other.changed
        this.created = other.created
        this.segment = other.segment
        this.title = other.title
        this.summary = other.summary
        return this
    }

    override fun toString() = "id=$id blog=$blog state=$state changed=$changed title=$title"
}