package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*
import java.time.Instant

//https://www.baeldung.com/kotlin/jpa
@Entity
class Blog(

    @Column(nullable = false, columnDefinition ="timestamp(0)")
    var changed: Instant,

    @Column(nullable = false, length = MAX_SEGMENT_SIZE)
    var segment: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic", nullable = false)
    var topic: Topic,

    @Column(nullable = false)
    var pos: Int,

    @Column(nullable = false, length = MAX_TITLE_SIZE)
    var subject: String,

    @Column(nullable = false, length = MAX_SUMMARY_SIZE)
    var about: String,

    @OneToMany(mappedBy = "blog", cascade = [CascadeType.ALL], orphanRemoval = true)
    var blogPosts: MutableList<BlogPost>,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_owner_id", nullable = false)
    var blogOwner: BlogOwner,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null,
) {

    fun copyAttributes(other: Blog): Blog {
        this.changed = other.changed
        this.segment = other.segment
        this.topic = other.topic
        this.pos = other.pos
        this.subject = other.subject
        this.about = other.about
        return this
    }


    override fun toString() = "id: $id segment: $segment subject: $subject" +
            "topic: ${topic.text} blogOwner: $blogOwner changed: $changed"
}