package com.sigmondsmart.edrops.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

///Just an example of setter injection
@Component
class ReadConfig2 {

    @Autowired
    private lateinit var config: AppConfig

    fun getConfig(): String? {
        log.info("App name: ${config.appname}")
        return config.appname
    }
}