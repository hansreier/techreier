package com.techreier.edrops.dto

import com.techreier.edrops.domain.Topic

data class MenuItem(
    val langCode: String,
    val segment: String,
    val topicKey: String,
    val subject: String,
    val isTopic: Boolean = false,
) {
    constructor(segment: String, topic: Topic, subject: String) :
            this(topic.language.code, segment, topic.topicKey, subject)
}