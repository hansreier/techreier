package com.techreier.edrops.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!local & !local-mariadb & !h2")
class BuildTimeValidation {

    @Autowired
    lateinit var appConfig: AppConfig

    @PostConstruct
    fun validateBuildTime() {
        if (appConfig.buildTime.isNullOrEmpty()) {
            throw IllegalStateException("BUILD_TIME environment variable must be set unless for specified local profiles")
        }
    }
}
