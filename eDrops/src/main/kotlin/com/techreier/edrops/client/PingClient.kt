package com.techreier.edrops.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class PingClient(builder: RestClient.Builder) {
    private val restClient = builder.build()

    fun ping(): String? =
        restClient.get().uri("/api/ping").retrieve().body(String::class.java)

    fun ping(text: String): String? =
        restClient.get().uri { it.path("/api/ping").queryParam("input", text).build() }
            .retrieve().body(String::class.java)
}