package com.techreier.edrops.client

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@SpringBootTest
@Import(AppConfig::class)
class SsbClientTest2IT {

    @Autowired
    private lateinit var ssbClient: SsbClient

    @Test
    fun ssbClientTest() {
        val json = ssbClient.fetchEnergyData()
        logger.info(json)
    }
}