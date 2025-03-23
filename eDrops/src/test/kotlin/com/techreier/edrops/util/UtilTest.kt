package com.techreier.edrops.util

import com.techreier.edrops.domain.NB
import com.techreier.edrops.dto.MenuItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import java.util.Locale

class UtilTest {

    @Test
    fun getEmptyMenuTest() {
        val origMenuItems = listOf<MenuItem>()
        val menuSplitSize = origMenuItems.size + 5
        val menuItems = getMenuItems( origMenuItems, menuSplitSize +1 ,1, messageSource)
        Assertions.assertEquals(0, menuItems.size)
    }

    @Test
    fun getMenuItemsWithTooSmallMenuToSplitTest() {
        val origMenuItems = Docs.about.asList().filter { doc -> doc.langCode == NB }
        val menuSplitSize = origMenuItems.size + 5
        val menuItems = getMenuItems( origMenuItems, menuSplitSize +1 ,1, messageSource)
        Assertions.assertEquals(origMenuItems.size, menuItems.size)
        Assertions.assertEquals(0, menuItems.count { menuItem -> menuItem.isTopic })
    }

    @Test
    fun getMenuItemsWithTopicLargeMenuTest() {
        val origMenuItems = Docs.about.asList().filter { doc -> doc.langCode == NB }
        val menuItems = getMenuItems( origMenuItems, 0 ,0, messageSource)
        Assertions.assertEquals(origMenuItems.size + 1, menuItems.size)
        Assertions.assertEquals(1, menuItems.count { menuItem -> menuItem.isTopic })
    }

    @Test
    fun getMenuItemsWithTopicTooSmallSubmenuTest() {
        val origMenuItems = Docs.about.asList().filter { doc -> doc.langCode == NB }
        val menuItems = getMenuItems( origMenuItems, 0 ,15, messageSource)
        Assertions.assertEquals(origMenuItems.size, menuItems.size)
        Assertions.assertEquals(0, menuItems.count { menuItem -> menuItem.isTopic })
    }


    @Test
    fun timeStampFromIso8601() {
        val timeStamp = timeStampAsString( "2025-03-22T18:36:08Z")
        Assertions.assertEquals("22.03.2025 18:36:08", timeStamp)
    }

    @Test
    fun invalidTimeStamp() {
        val timeStamp = timeStampAsString("2025-03-22T18:36:08")
        Assertions.assertEquals("", timeStamp)
    }

    @Test
    fun emptyTimeStamp() {
        val timeStamp = timeStampAsString("")
        Assertions.assertEquals("", timeStamp)
    }

    @Test
    fun nullTimeStamp() {
        val timeStamp = timeStampAsString(null)
        Assertions.assertEquals("", timeStamp)
    }


    // Messagesource is redefined so not fetched from disk
    private val messageSource = object : MessageSource {
        override fun getMessage(code: String, args: Array<out Any>?, defaultMessage: String?, locale: Locale): String {
            return code + "." + locale.language
        }

        override fun getMessage(code: String, args: Array<out Any>?, locale: Locale): String {
            return code + "." + locale.language
        }

        override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
            return resolvable.defaultMessage ?: resolvable.codes?.firstOrNull() ?: "??${resolvable.codes?.firstOrNull()}??"
        }
    }
}