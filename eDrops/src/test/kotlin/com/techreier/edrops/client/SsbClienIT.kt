package com.techreier.edrops.client

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class SsbClienIT {

    private val restClient = RestClient.create()

    private val ssbClient = SsbClient(restClient)

    @Test
    fun ssbClientTest() {
        val json = ssbClient.fetchEnergyData()
        logger.info(json)
    }
}