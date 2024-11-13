package com.techreier.edrops

import com.techreier.edrops.config.logger
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("gensql")
class SqlGenerateIT {

    //Generates SQL from object model with JPA/Hibernate in file create.sql
    @Test
    fun testSqlGenerate() {
        logger.info("test generate SQL from JPA/Hibernate domain model")
    }
}
