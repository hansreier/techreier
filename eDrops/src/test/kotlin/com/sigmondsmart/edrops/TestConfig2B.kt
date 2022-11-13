package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.ReadConfig2
import com.sigmondsmart.edrops.config.logger
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestConfig2B {


    @Test
    @Disabled
    fun testAppName() {
        val readConfig = ReadConfig2()
        readConfig.config.appname ="ReierApp"
        logger.info("tested ${readConfig.config.appname}")
    }
}