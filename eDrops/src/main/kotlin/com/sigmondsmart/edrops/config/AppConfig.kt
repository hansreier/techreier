package com.sigmondsmart.edrops.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotNull

@Component
@ConfigurationProperties(prefix = "app")
@Validated //Checks if properties exist in combination with @NotNull and aborts if not
class AppConfig {
    @NotNull(message = "Mangler navn på applikasjon") //Checks if properties exist
    var appname: String? = null
}