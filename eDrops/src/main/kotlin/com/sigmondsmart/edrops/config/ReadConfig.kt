package com.sigmondsmart.edrops.config

import org.springframework.stereotype.Component

///Just an example of constructor injection
@Component
class ReadConfig(private val config: AppConfig) {

    fun getConfig(): String? {
        logger.info("App name: ${config.appname}")
        return config.appname
    }
}