package com.techreier.edrops.util

import com.techreier.edrops.data.Docs
import com.techreier.edrops.data.NB
import com.techreier.edrops.dto.MenuItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.context.MessageSourceResolvable
import java.util.Locale

class UtilTest {

    private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")

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
        val menuItems = getMenuItems( origMenuItems, 0 ,16, messageSource)
        Assertions.assertEquals(origMenuItems.size, menuItems.size)
        Assertions.assertEquals(0, menuItems.count { menuItem -> menuItem.isTopic })
    }

    @Test
    fun buildVersionFromIso8601WinterTest() {
        val timeStamp = buildVersion( "2025-03-22T18:36:08Z", false)
        Assertions.assertEquals("250322193608", timeStamp)
    }

    @Test
    fun buildVersionFromIso8601SummerTest() {
        val timeStamp = buildVersion( "2025-04-22T18:36:08Z", false)
        Assertions.assertEquals("250422203608", timeStamp)
    }

    @Test
    fun buildShortVersionFromIso8601Test() {
        val timeStamp = buildVersion( "2025-03-22T18:36:08Z", true)
        Assertions.assertEquals("22.03.2025", timeStamp)
    }

    @Test
    fun invalidTimeStamp() {
        val timeStamp = buildVersion("2025-03-22T18:36:08", false)
        Assertions.assertEquals("", timeStamp)
    }

    @Test
    fun emptyTimeStamp() {
        val timeStamp = buildVersion("")
        assertThat(timeStamp).isNotNull()
        assertThat(timeStamp).isNotEmpty()
    }

    @Test
    fun nullTimeStamp() {
        val timeStamp = buildVersion(null, false)
        assertThat(timeStamp).isNotNull()
        assertThat(timeStamp).isNotEmpty()
        logger.info("Current timestamp {}: {}", TIMESTAMP_PATTERN, timeStamp)
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
            return resolvable.defaultMessage ?: resolvable.codes?.firstOrNull() ?: "??${resolvable.codes?.firstOrNull()}??"
        }
    }
}