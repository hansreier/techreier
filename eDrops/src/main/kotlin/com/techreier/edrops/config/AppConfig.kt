package com.techreier.edrops.config

import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

// This way of reading app properties is more flexible and easier than using @Value in Kotlin
// Disadvantage: Have to inject this always
@Configuration
@ConfigurationProperties(prefix = "app")
@Validated //Checks if properties exist in combination with @NotNull and aborts if not
class AppConfig {
    @NotNull(message = "Mangler navn på applikasjon") //Checks if properties exist
    var appname: String? = null
    @NotNull(message = "Missing admin password")
    var password: String? = null
}
