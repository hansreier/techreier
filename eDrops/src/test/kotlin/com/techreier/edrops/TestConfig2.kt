package com.techreier.edrops

import com.techreier.edrops.config.ReadConfig2
import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TestConfig2 {

    @Autowired
    private lateinit var readConfig2: ReadConfig2
    @Test
    fun testAppName() {
        logger.info("App name: ${readConfig2.getConfig()}")
    }
}