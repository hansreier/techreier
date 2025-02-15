
package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*

// @Table(uniqueConstraints = [UniqueConstraint(columnNames = ["topic", "language"])])
@Entity
class Topic(
    @Column(nullable = false, length = MAX_SEGMENT_SIZE)
    val topicKey: String,
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_code", nullable = false)
    var language: LanguageCode,
    @Column(nullable = true, length = MAX_TITLE_SIZE)
    var text: String? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
) {
    override fun toString() = "key: $topicKey topic: $text language: ${language.language} id: $id"
}
