package com.techreier.edrops

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class TestLogger {
    @BeforeEach
    fun setUp() {
        logger.info("BeforeEach")
    }
    @Test
    fun testLogger() {
        println ("Console only")
        logger.warn("Warn level")
        logger.info("Info level")
        logger.debug("Debug level")
        logger.trace("Trace level")
    }
}
