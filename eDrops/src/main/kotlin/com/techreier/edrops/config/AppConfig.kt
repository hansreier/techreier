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
const val MAX_TITLE_SIZE = 80
const val MAX_SUMMARY_SIZE = 1000
const val MAX_CODE_SIZE = 20
const val MAX_USERNAME_SIZE = 40
const val DEFAULT_TIMEZONE = "Europe/Oslo"
const val DOUBLE_FLOAT_PRECISION_DEFAULT = 5
const val DOUBLE_FIXED_PRECISION_DEFAULT = 1
const val TWH_PRECISION_DEFAULT = 1

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
