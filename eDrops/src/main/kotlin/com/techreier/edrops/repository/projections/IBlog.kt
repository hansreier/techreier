package com.techreier.edrops.repository.projections

import java.time.Instant

interface IBlog {
    val changed: Instant
    val segment: String
    val topicKey: String
    val topicText: String?
    val languageCode: String
    val subject: String
    val about: String
    val pos: Int
    val id: Long
}

