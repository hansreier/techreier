package com.techreier.edrops.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class PingClient(builder: RestClient.Builder) {
    private val restClient = builder.build()

    fun ping(): String? =
        restClient.get().uri("/api/ping").retrieve().body<String>()

    fun ping(text: String): String? =
        restClient.get().uri { it.path("/api/ping").queryParam("input", text).build() }
            .retrieve().body<String>()
}