package com.sigmondsmart.edrops.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

//Just a test of @Value really, no need for @Configuration and define bean. TODO remove later
@Component
class AppConfigNew {

    @Value("\${app.appname}")
    private val appName: String? = null

    fun config(): String? {
        log.info("Reier App name: ${appName}")
        return appName
    }

}