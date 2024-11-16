package com.techreier.edrops.util

import com.techreier.edrops.domain.LanguageCode
data class Doc(val segment: String, val language: LanguageCode, val subject: String? = null, val ext: Boolean = true)
