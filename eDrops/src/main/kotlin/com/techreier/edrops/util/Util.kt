package com.techreier.edrops.util

import com.techreier.edrops.config.DEFAULT_TIMEZONE
import com.techreier.edrops.dto.MenuItem
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")
const val DATE_PATTERN = "dd.MM.yyyy"
const val TIMESTAMP_PATTERN = "yyMMddHHmmss"

// Return built version as simple timestamp with minutes or date. If time is not there current time is used.
// timestamp used for caching of frontend files, this is the reason for reversed format without separators.
// timestamp returnes is Oslo time.
fun buildVersion(utc: String?, short: Boolean = true): String {
    return try {
        if (utc.isNullOrBlank()) {
            DateTimeFormatter.ofPattern(if (short) DATE_PATTERN else TIMESTAMP_PATTERN).format(timeStampDefaultTimezone())
        } else {
            val defaultTime = Instant.parse(utc).atZone(ZoneId.of(DEFAULT_TIMEZONE))
            DateTimeFormatter.ofPattern(if (short) DATE_PATTERN else TIMESTAMP_PATTERN).format(defaultTime)
        }
    } catch (ex: DateTimeParseException) {
        logger.error("${ex.message} returning empty string")
        ""
    }
}

//Only correct in default timezone, Norway. So only use in tests and for buildVersion
fun timeStampDefaultTimezone(): ZonedDateTime = ZonedDateTime.now(ZoneId.of(DEFAULT_TIMEZONE)).truncatedTo(ChronoUnit.SECONDS)

// Return valid language code actually used in this project based on rule
// If language code does not exist return default (english)
fun getValidProjectLanguageCode(languageCode: String): String =
    if (languageCode in listOf("nn", "no", "nb")) "nb" else "en"

// Function assumes menu items (blogs) to be sorted by Topic position and MenuItem position.
// If desired topics is added to the menu with items underneath it
// TOPIC_ITEMS_MINIMUM decides this criteria.
fun getMenuItems(
    menuItemOrig: List<MenuItem>, submenuMinItems: Int, menuSplitSize: Int,
    messageSource: MessageSource,
): List<MenuItem> {

    val menuItems = mutableListOf<MenuItem>()
    var previousTopic = ""
    var endPos = 0
    val size = menuItemOrig.size
    val split = size >= menuSplitSize
    var count = 0

    menuItemOrig.forEachIndexed { index, menuItem ->

        if ((menuItem.topicKey != previousTopic) && split) {
            if (previousTopic.isNotEmpty()) {
                if (endPos == 0) endPos = menuItems.size

                var pos = index
                val firstPos = pos
                do {
                    pos++
                } while ((pos < size) && (menuItemOrig[pos].topicKey == menuItem.topicKey))
                count = pos - firstPos
                if (count >= submenuMinItems) { // Eventually add topic to menu
                    count = 0
                    menuItems.add(
                        MenuItem(
                            menuItem.langCode, "#${menuItem.topicKey}", menuItem.topicKey,
                            msg(messageSource, "topic.${menuItem.topicKey}"), true
                        )
                    )
                }
            }
            previousTopic = menuItem.topicKey
        }

        if (count > 0) { // Move menu item up if topic is not added to menu
            menuItems.add(endPos, menuItem)
            endPos++
            count--
        } else {
            menuItems.add(menuItem)
        }
    }

    return menuItems
}

// Spring localized messages given messageSource, key and arguments
fun msg(messageSource: MessageSource, key: String, args: Array<Any>? = null): String {
    val locale = LocaleContextHolder.getLocale()
    return messageSource.getMessage(key, args, "??$key??", locale) as String
}