package com.techreier.edrops.util

import com.techreier.edrops.domain.Topic

data class Doc(val segment: String, val topic: Topic, val subject: String? = null, val langcode_ext: Boolean = true)
