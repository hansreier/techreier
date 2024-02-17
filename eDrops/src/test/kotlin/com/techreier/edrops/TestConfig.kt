package com.techreier.edrops

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TestConfig {

    @Autowired
    private lateinit var appConfig: AppConfig

    @Value("\${app.appname}")
    private val appname: String = ""

    @Test
    fun testAppConfig() {
        logger.info("App name: ${appConfig.appname}")
        Assertions.assertEquals(appname, appConfig.appname)
        Assertions.assertNotNull(appConfig.password)
    }
}
