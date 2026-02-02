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


    fun ping(text: String? = null): String? {
        return restClient.get()
            .uri { uriBuilder ->
                val builder = uriBuilder.path("/api/ping")
                if (text != null) {
                    builder.queryParam("input", text)
                }
                builder.build()
            }
            .retrieve()
            .body<String>()
    }
}