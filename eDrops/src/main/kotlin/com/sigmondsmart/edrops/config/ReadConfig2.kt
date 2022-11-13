package com.sigmondsmart.edrops.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

//Just an example of field based injection
@Component
class ReadConfig2 {

    @Autowired
    lateinit var config: AppConfig

    fun getConfig(): String? {
        logger.info("App name: ${config.appname}")
        return config.appname
    }
}