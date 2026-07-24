package com.techreier.edrops.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class UtilTest {

    private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")

    @Test
    fun buildVersionFromIso8601WinterTest() {
        val timeStamp = buildVersion("2025-03-22T18:36:08Z", false)
        assertEquals("250322193608", timeStamp)
    }

    @Test
    fun buildVersionFromIso8601SummerTest() {
        val timeStamp = buildVersion("2025-04-22T18:36:08Z", false)
        assertEquals("250422203608", timeStamp)
    }

    @Test
    fun buildShortVersionFromIso8601Test() {
        val timeStamp = buildVersion("2025-03-22T18:36:08Z", true)
        assertEquals("22.03.2025", timeStamp)
    }

    @Test
    fun invalidTimeStamp() {
        val timeStamp = buildVersion("2025-03-22T18:36:08", false)
        assertEquals(12, timeStamp.length)
        assertThat(timeStamp.toLongOrNull()).isNotNull()
    }

    @Test
    fun emptyTimeStamp() {
        val timeStamp = buildVersion("", true)
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

}