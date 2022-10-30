package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.log
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class TestLogger {
    @BeforeEach
    fun setUp() {
        log.info("BeforeEach")
    }
    @Test
    fun testLogger() {
        println ("Reier")
        log.info("Reier was here")
        log.debug("Test av logg level")
    }
}
