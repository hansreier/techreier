package com.techreier.edrops.util

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// Return current zoned time
fun timeStamp(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)

// Return valid language code actually used in this project based on rule
// If language code does not exist return default (english)
fun validProjectLanguageCode(languageCode: String): String = if (languageCode in listOf("nn", "no", "nb")) "nb" else "en"