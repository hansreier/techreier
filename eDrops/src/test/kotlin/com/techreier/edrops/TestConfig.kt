package com.techreier.edrops

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.ReadConfig
import com.techreier.edrops.config.logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TestConfig {
    @Autowired
    private lateinit var appConfig: AppConfig

    @Autowired
    private lateinit var readConfig: ReadConfig

    @BeforeEach
    fun setUp() {
        readConfig =  ReadConfig(config = appConfig)
    }

    @Test
    fun testAppNameIndirect() {
        logger.info("App name: ${readConfig.getConfig()}")
    }

    @Test
    fun testAppNameDirect() {
        logger.info("App name: ${appConfig.appname}")
    }
}
