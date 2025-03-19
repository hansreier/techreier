package com.techreier.edrops.util

import com.techreier.edrops.dto.MenuItem
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// Return current zoned time
fun timeStamp(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)

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