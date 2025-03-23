package com.techreier.edrops.util

import com.techreier.edrops.dto.MenuItem
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")
private const val DATE_PATTERN = "dd.MM.yyyy"
private const val DATETIME_PATTERN = "dd.MM.yyyy HH:mm:ss"

// Return curent Zoned time
fun timeStamp(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)

// Return  time from UTC
fun timeStamp(utc: String): ZonedDateTime =  ZonedDateTime.parse(utc).truncatedTo(ChronoUnit.SECONDS)

// return built version as datetime or date
fun buildVersion(utc: String?, short: Boolean = true): String {
    try {
        return utc?.let { DateTimeFormatter.ofPattern(if (short) DATE_PATTERN else DATETIME_PATTERN).format(timeStamp(utc)) }
            ?: throw DateTimeParseException("No timestamp to parse","",0)
    } catch (ex: DateTimeParseException) {
        logger.warn("${ex.message} returning empty string")
        return ""
    }
}

// Return valid language code actually used in this project based on rule
// If language code does not exist return default (english)
fun validProjectLanguageCode(languageCode: String): String = if (languageCode in listOf("nn", "no", "nb")) "nb" else "en"

// Function assumes menu items (blogs) to be sorted by Topic position and MenuItem position.
// If desired topics is added to the menu with items underneath it
// TOPIC_ITEMS_MINIMUM decides this criteria.
// TODO Method is suitable for unit test
fun getMenuItems(menuItemOrig: List<MenuItem>, submenuMinItems: Int, menuSplitSize: Int,
                 messageSource: MessageSource
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
                } while ((pos < size) && (menuItemOrig[pos].topicKey == menuItem.topicKey) )
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

fun msg(messageSource: MessageSource, key: String): String {
    val locale = LocaleContextHolder.getLocale()
    return messageSource.getMessage(key, null, "??$key??", locale) as String
}