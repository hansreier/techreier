package com.techreier.edrops.client

import com.techreier.edrops.config.PONG_TEXT
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PingClientIT {

    @Autowired
    private lateinit var pingClient: PingClient

    @Test
    fun `skal kalle ping på kjørende server`() {
        val respons = pingClient.ping()
        assertEquals(PONG_TEXT, respons)
    }

    @Test
    fun `skal kalle ping med parameter på kjørende server`() {
        val respons = pingClient.ping("Reier")
        assertEquals("$PONG_TEXT: Reier", respons)
    }
}