package com.techreier.edrops.config

import org.springframework.boot.restclient.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory

@Configuration
class RestClientConfig {

    @Bean
    fun restClientCustomizer(): RestClientCustomizer {
        return RestClientCustomizer { builder ->
            builder.requestFactory(HttpComponentsClientHttpRequestFactory())
                .baseUrl("http://localhost:8080")
        }
    }
}