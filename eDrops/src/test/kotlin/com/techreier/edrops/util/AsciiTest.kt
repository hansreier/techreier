package com.techreier.edrops.util

import com.techreier.edrops.data.blogs.coding.ZeroTrust
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class AsciiTest {

    private val logger = LoggerFactory.getLogger("com.techreier.edrops.util")


    @Test
    fun asciiMarkdownTest() {
        val output = asciiToTable(
            source = ZeroTrust.SUMMARY_NO,
            presenter  = ::asciiMarkdown
        )
        logger.info("\n${output}")
    }

    @Test
    fun asciiLogTest() {
        val output = asciiToTable(
            source = ZeroTrust.SUMMARY_NO,
            presenter = ::asciiLog
        )
        logger.info("\n${output}")
    }

}