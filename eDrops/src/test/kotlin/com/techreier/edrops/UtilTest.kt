package com.techreier.edrops

import com.techreier.edrops.domain.NB
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.getMenuItems
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import java.util.*

class UtilTest {

    @Test
    fun getEmptyMenuTest() {
        val origMenuItems = listOf<MenuItem>()
        val menuSplitSize = origMenuItems.size + 5
        val menuItems = getMenuItems( origMenuItems, menuSplitSize +1 ,1, messageSource)
        assertEquals(0, menuItems.size)
    }

    @Test
    fun getMenuItemsWithTooSmallMenuToSplitTest() {
        val origMenuItems = Docs.about.asList().filter { doc -> doc.langCode == NB}
        val menuSplitSize = origMenuItems.size + 5
        val menuItems = getMenuItems( origMenuItems, menuSplitSize +1 ,1, messageSource)
        assertEquals(origMenuItems.size, menuItems.size)
        assertEquals(0, menuItems.count { menuItem -> menuItem.isTopic})
    }

    @Test
    fun getMenuItemsWithTopicLargeMenuTest() {
        val origMenuItems = Docs.about.asList().filter { doc -> doc.langCode == NB}
        val menuItems = getMenuItems( origMenuItems, 0 ,0, messageSource)
        assertEquals(origMenuItems.size + 1, menuItems.size)
        assertEquals(1, menuItems.count { menuItem -> menuItem.isTopic})
    }

    @Test
    fun getMenuItemsWithTopicTooSmallSubmenuTest() {
        val origMenuItems = Docs.about.asList().filter { doc -> doc.langCode == NB}
        val menuItems = getMenuItems( origMenuItems, 0 ,15, messageSource)
        assertEquals(origMenuItems.size, menuItems.size)
        assertEquals(0, menuItems.count { menuItem -> menuItem.isTopic})
    }


    // Messagesource is redefined so not fetched from disk
    private val messageSource = object : MessageSource {
        override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String {
            return code + "." + locale.language
        }

        override fun getMessage(code: String, args: Array<out Any>?, locale: Locale): String {
            return code + "." + locale.language
        }

        override fun getMessage(resolvable: org.springframework.context.MessageSourceResolvable, locale: Locale): String {
            return resolvable.defaultMessage ?: resolvable.codes?.firstOrNull() ?: "??${resolvable.codes?.firstOrNull()}??"
        }
    }
}