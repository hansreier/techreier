package com.techreier.edrops.dto

import com.techreier.edrops.domain.Topic

data class BlogParams(
    val blog: BlogDTO?,
    val oldLangCode: String?,
    val usedLangCode: String,
    val action: String,
    val topicKey: String,
    val topics: List<Topic>,
) {
    override fun toString() =
        "action=$action, key=$topicKey, lang=$oldLangCode=>$usedLangCode, segment=${blog?.segment}"
}