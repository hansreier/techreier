package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.AppConfigNew
import com.sigmondsmart.edrops.config.log
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TestConfigB {

    @Autowired
    private lateinit var appConfig: AppConfigNew

    @Test
    fun testAppName() {
        log.info("App name: ${appConfig.config()}")
    }
}