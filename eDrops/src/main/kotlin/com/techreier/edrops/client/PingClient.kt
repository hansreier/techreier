package com.techreier.edrops.client

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class PingClient(
    restClientBuilder: RestClient.Builder
) {
    private val restClient = restClientBuilder
        .baseUrl("http://localhost:8080")
        .build()


    fun ping(): String? {
        val result = restClient.get()
            .uri("/api/ping")
            .retrieve()
            .body<String>()
        return result
    }
}