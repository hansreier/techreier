package com.techreier.edrops.util

import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.dto.MenuItem
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")

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
            var pos = index
            val firstPos = pos
            do {
                pos++
            } while ((pos < size) && (menuItemOrig[pos].topicKey == menuItem.topicKey))
            count = pos - firstPos
            if (count >= submenuMinItems) { // Eventually add topic to menu
                count = 0
                if (menuItem.topicKey != TOPIC_DEFAULT) {
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
    logger.info("menuItemsOrig: {}", menuItemOrig)
    logger.info("menuTtems: $menuItems")
    return menuItems
}