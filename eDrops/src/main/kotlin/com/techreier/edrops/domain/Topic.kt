
package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*

@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["topic_key", "language_code"])])
@Entity
class Topic(
    @Column(nullable = false, length = MAX_SEGMENT_SIZE)
    val topicKey: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_code", nullable = false)
    var language: LanguageCode,

    @Column(nullable = false)
    var pos: Int  = 0,

    @Column(nullable = true, length = MAX_TITLE_SIZE)
    var text: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {

    fun copyAttributes(other: Topic): Topic {
        this.language = other.language
        this.pos = other.pos
        this.text = other.text
        return this
    }

    override fun toString() = "key=$topicKey language=${language.language} id=$id"
}
