package com.sigmondsmart.edrops

import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory


class TestReier {

   private val log = LoggerFactory.getLogger(TestReier::class.java)

    @Test
    fun testDummy() {
        println ("Reier")
        log.info("Reier was here")
        log.debug("Test av logg level")
    }
}
