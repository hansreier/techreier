package com.techreier.edrops.dto

import com.techreier.edrops.domain.Topic

data class MenuItem(
    val langCode: String,
    val segment: String,
    val topicKey: String,
    val subject: String,
    val langCodeExt: Boolean = true,
    val isTopic: Boolean = false
) {
    constructor(segment: String, topic: Topic, subject: String, langCodeExt: Boolean = true) :
            this(topic.language.code, segment, topic.topicKey, subject, langCodeExt)
}