package com.techreier.edrops.config

import com.techreier.edrops.data.Base
import com.techreier.edrops.data.Initial
import com.techreier.edrops.dbservice.InitService
import com.techreier.edrops.util.buildVersion
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class Init(
    appConfig: AppConfig,
    initService: InitService,
) {
    init {
        val buildVersion = buildVersion(appConfig.buildTime, false)
        logger.info("App name: ${appConfig.appname} built: ${buildVersion}")
        if (!appConfig.auth) {
            logger.warn("Admin user auth is off, turn on and redeploy if production")
        }
        val base = Base()
        val initial = Initial(appConfig, base)
        initService.saveInitialData(initial)
    }
}

