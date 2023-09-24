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
        println ("Reier")
        logger.info("Reier was here")
        logger.debug("Test av logg level")
    }
}
