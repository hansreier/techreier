
package com.techreier.edrops.domain

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import jakarta.persistence.*

@Entity
//@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["topic", "language"])])
class Topic(

    @Column(nullable = false, length = MAX_SEGMENT_SIZE)
    val keyWord: String,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "language_code", nullable = false)
    var language: LanguageCode,

    @Column(nullable = true, length = MAX_TITLE_SIZE)
    val text: String? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long?=null
) {
    companion object {
        const val DEFAULT="default"
        const val CODING="coding"
        const val ENERGY="energy"
      //  val ALL = listOf(DEFAULT_EN,DEFAULT_NO,CODING_EN,CODING_NO,ENERGY_EN,ENERGY_NO)

        fun create(keyword: String, language: LanguageCode):Topic {
            return Topic(keyword, language)
        }

    }

    override fun toString() = "key: $keyWord topic: $text: $text language: ${language.language} id: $id"
}