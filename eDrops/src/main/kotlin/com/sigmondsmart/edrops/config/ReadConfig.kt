package com.sigmondsmart.edrops.config

import org.springframework.stereotype.Component

@Component
class ReadConfig(private val config: AppConfig) {

    fun getConfig(): String? {
        log.info("App name: ${config.appname}")
        return config.appname
    }
}