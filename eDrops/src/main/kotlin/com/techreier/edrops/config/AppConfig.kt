package com.techreier.edrops.config

import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.annotation.RollbackOn
import org.springframework.validation.annotation.Validated
import org.springframework.web.client.RestClient

const val MAX_SEGMENT_SIZE = 30
const val MAX_TITLE_SIZE = 50
const val MAX_SUMMARY_SIZE = 400
const val MAX_CODE_SIZE = 15
const val MAX_USERNAME_SIZE = 60
const val DEFAULT_TIMEZONE = "Europe/Oslo"

// This way of reading app properties is more flexible and easier than using @Value in Kotlin
// Disadvantage: Have to inject this always
@Configuration
@ConfigurationProperties(prefix = "app")
@EnableTransactionManagement(rollbackOn = RollbackOn.ALL_EXCEPTIONS)
@Validated // Checks if properties exist in combination with @NotNull and aborts if not
class AppConfig {
    @NotNull(message = "Mangler navn på applikasjon") // Checks if properties exist
    lateinit var appname: String

    @NotNull(message = "Missing admin password")
    lateinit var password: String

    @NotNull(message = "Missing admin user")
    lateinit var user: String

    var auth: Boolean = true

     val buildTime = System.getenv("BUILD_TIME")

    @Bean
    fun restClient(): RestClient = RestClient.create()
}
