package com.techreier.edrops.util

import com.techreier.edrops.data.NB
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.data.TOPIC_LEISURE
import com.techreier.edrops.data.TOPIC_POLITICS
import com.techreier.edrops.data.TOPIC_TECH
import com.techreier.edrops.dto.MenuItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import java.util.*

class MenuTest {

    @Test
    fun getEmptyMenuTest() {
        val origMenuItems = listOf<MenuItem>()
        val menuSplitSize = origMenuItems.size + 5
        val menuItems = getMenuItems(origMenuItems, menuSplitSize + 1, 1, messageSource)
        assertEquals(0, menuItems.size)
    }

    @Test
    fun getMenuItemsWithTooSmallMenuToSplitTest() {
        val origMenuItems = populateMenuItems(
            noDefault = 0,
            noTech = 1,
            noLeisure = 1,
            noPolitics = 1
        )
        val menuSplitSize = origMenuItems.size
        val menuItems = getMenuItems(origMenuItems, menuSplitSize + 1, 1, messageSource)
        assertThat(menuItems).containsExactlyElementsOf(origMenuItems)
    }

    @Test
    fun getMenuItemsWith2TopicsLargeTest() {
        val origMenuItems = populateMenuItems(
            noDefault = 0,
            noTech = 2,
            noLeisure = 2,
            noPolitics = 1
        )
        val menuItems = getMenuItems(origMenuItems, 2, 0, messageSource)
        assertEquals(origMenuItems.size + 2, menuItems.size)
        assertEquals(2, menuItems.count { menuItem -> menuItem.isTopic })
        assertEquals(1, noTop(menuItems))
    }

    @Test
    fun getMenuItemsExactMatchTest() {
        val origMenuItems = populateMenuItems(
            noDefault = 1,
            noTech = 1,
            noLeisure = 2,
            noPolitics = 1
        )
        val menuItems = getMenuItems(origMenuItems, 2, 0, messageSource)
        assertEquals(origMenuItems.size + 1, menuItems.size)
        assertEquals(origMenuItems[0], menuItems[0])
        assertEquals(origMenuItems[1], menuItems[1])
        assertEquals(origMenuItems[4], menuItems[2])
        assertTrue(menuItems[3].isTopic)
        assertEquals(origMenuItems[2], menuItems[4])
        assertEquals(origMenuItems[3], menuItems[5])
    }

    @Test
    fun getMenuItemsWith31TopicsLargeTest() {
        val origMenuItems = populateMenuItems(
            noDefault = 0,
            noTech = 2,
            noLeisure = 2,
            noPolitics = 2
        )
        val menuItems = getMenuItems(origMenuItems, 2, 0, messageSource)
        assertEquals(origMenuItems.size + 3, menuItems.size)
        assertEquals(3, menuItems.count { menuItem -> menuItem.isTopic })
        assertEquals(0, noTop(menuItems))
    }

    // Messagesource is redefined so not fetched from disk
    val messageSource = object : MessageSource {
        override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale?): String {
            return code + "." + locale?.language
        }

        override fun getMessage(code: String, args: Array<out Any>?, locale: Locale?): String {
            return code + "." + locale?.language
        }

        override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale?): String {
            return resolvable.defaultMessage ?: resolvable.codes?.firstOrNull()
            ?: "??${resolvable.codes?.firstOrNull()}??"
        }
    }

    private fun populateMenuItems(noDefault: Int, noTech: Int, noLeisure: Int, noPolitics: Int): List<MenuItem> {
        val menuItems = mutableListOf<MenuItem>()
        for (i in 1..noDefault) {
            menuItems.add(MenuItem(
                langCode = NB,
                segment = "$TOPIC_DEFAULT$i",
                topicKey = TOPIC_DEFAULT,
                subject = "$TOPIC_DEFAULT$i"))
        }
        for (i in 1..noTech) {
            menuItems.add(MenuItem(
                langCode = NB,
                segment ="$TOPIC_TECH$i",
                topicKey = TOPIC_TECH,
                subject = "C$TOPIC_TECH"))
        }
        for (i in 1..noLeisure) {
            menuItems.add(MenuItem(
                langCode = NB,
                segment ="$TOPIC_LEISURE$i",
                topicKey =TOPIC_LEISURE,
                subject = "$TOPIC_LEISURE$i"))
        }
        for (i in 1..noPolitics) {
            menuItems.add(MenuItem(
                langCode = NB,
                segment = "$TOPIC_POLITICS$i",
                topicKey = TOPIC_POLITICS,
                subject = "$TOPIC_POLITICS$i"))
        }
        return menuItems
    }

    private fun noTop(menuItems: List<MenuItem>): Int {
        var no  = 0
        while (!menuItems[no].isTopic && no < menuItems.size) { no++}
        return no
    }

}