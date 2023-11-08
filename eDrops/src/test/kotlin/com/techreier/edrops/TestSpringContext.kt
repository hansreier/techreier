package com.techreier.edrops

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
class TestSpringContext {

    @Test
    fun testSpringContext() {
        logger.info("Hello Spring")
    }
}
