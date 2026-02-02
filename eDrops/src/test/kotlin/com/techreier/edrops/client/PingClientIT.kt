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
        // Forventer at serveren kjører på den URL-en som er konfigurert i PingClient
        val respons = pingClient.ping()
        assertEquals(PONG_TEXT, respons)
    }
}