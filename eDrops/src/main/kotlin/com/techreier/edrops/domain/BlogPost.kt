package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*
import org.hibernate.annotations.TimeZoneStorage
import org.hibernate.annotations.TimeZoneStorageType
import java.time.ZonedDateTime

//https://www.baeldung.com/kotlin/jpa
@Entity
class BlogPost(

    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Column(columnDefinition = "timestamp(0)")
    var changed: ZonedDateTime?,

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
    val id: Long?=null
) {
    override fun toString() = "id: $id blog: $blog changed: $changed text: $title"
}