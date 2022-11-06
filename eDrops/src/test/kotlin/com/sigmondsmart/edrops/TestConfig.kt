package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.AppConfig
import com.sigmondsmart.edrops.config.ReadConfig
import com.sigmondsmart.edrops.config.log
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
        log.info("App name: ${readConfig.getConfig()}")
    }

    @Test
    fun testAppNameDirect() {
        log.info("App name: ${appConfig.appname}")
    }
}
