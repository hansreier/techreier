package com.techreier.edrops.util

import com.techreier.edrops.config.DEFAULT_TIMEZONE
import com.techreier.edrops.config.DOUBLE_FIXED_PRECISION_DEFAULT
import com.techreier.edrops.config.DOUBLE_FLOAT_PRECISION_DEFAULT
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.util.StringUtils
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")
const val DATE_PATTERN = "dd.MM.yyyy"
const val TIMESTAMP_PATTERN = "yyMMddHHmmss"

// Return built version as simple timestamp with minutes or date. If time is not there current time is used.
// timestamp used for caching of frontend files, this is the reason for reversed format without separators.
// timestamp returnes is Oslo time.
fun buildVersion(utc: String?, short: Boolean = false): String {
    val instant = try {
        if (!utc.isNullOrBlank()) {
            Instant.parse(utc)
        } else {
            Instant.now()
        }
    } catch (ex: DateTimeParseException) {
        logger.error("${ex.message}, falling back to current time")
        Instant.now()
    }

    val pattern = if (short) DATE_PATTERN else TIMESTAMP_PATTERN
    return DateTimeFormatter.ofPattern(pattern).format(instant.atZone(ZoneId.of(DEFAULT_TIMEZONE)))
}

fun timestamp(datetime: String): Instant {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
    return LocalDateTime.parse(datetime, formatter).atZone(ZoneId.of(DEFAULT_TIMEZONE)).toInstant()
}

// Return valid language code actually used in this project based on rule
// If language code does not exist return default (english)
fun getValidProjectLanguageCode(languageCode: String): String =
    if (languageCode in listOf("nn", "no", "nb")) "nb" else "en"


// Spring localized message given messageSource, key and arguments
fun msg(messageSource: MessageSource, key: String, args: Array<Any>? = null): String {
    val locale = LocaleContextHolder.getLocale()
    return messageSource.getMessage(key, args, "??$key??", locale) as String
}

// Spring localized message given messageSource, key and language code
fun msg(messageSource: MessageSource, key: String, languageCode: String): String {
    val locale = StringUtils.parseLocaleString(languageCode)
    return messageSource.getMessage(key, null, "??$key??", locale) as String
}

// Formatting functions

// Return a formatted String of date given a pattern
fun ZonedDateTime?.text(datePattern: String): String {
    val locale = LocaleContextHolder.getLocale()
    val formatter = DateTimeFormatter.ofPattern(datePattern, locale)
    return this?.format(formatter) ?: ""
}

fun Double?.fixed(precision: Int = DOUBLE_FIXED_PRECISION_DEFAULT): String {
    val locale = LocaleContextHolder.getLocale()
    return this ?.let { String.format(locale, "%.${precision}f", this)} ?: ""
}

fun Double?.float(precision: Int = DOUBLE_FLOAT_PRECISION_DEFAULT): String {
    val locale = LocaleContextHolder.getLocale()
    return this ?.let { String.format(locale, "%.${precision}g", this)} ?: ""
}

fun String.strip(): String {
    val result = this.replaceFirst('\n'.toString(),"")
    return result
}