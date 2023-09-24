package com.techreier.edrops.config

import org.springframework.stereotype.Component

// TODO Remove later, only used in unit test
// Just an example of constructor injection
@Component
class ReadConfig(private val config: AppConfig) {

    fun getConfig(): String? {
        logger.info("App name: ${config.appname}")
        return config.appname
    }
}