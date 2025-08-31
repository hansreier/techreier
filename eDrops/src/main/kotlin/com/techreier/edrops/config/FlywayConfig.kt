package com.techreier.edrops.config

import org.flywaydb.core.Flyway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Profile(
    value = [
        "test",
        "h2",
        "h2-disk",
        "h2-prod",
        "local-mariadb",
        "docker-mariadb",
        "mariadb-dockerized",
        "global-mariadb"
    ]
)

// Note: Required since Flyway v 11.5 work around.
// It was enough earlier to set Flyway to true in yaml (no turned off)
@Configuration
class FlywayConfig {

    @Bean
    @DependsOn("dataSource")
    fun flyway(dataSource: DataSource?): Flyway {
        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .load()
        flyway.migrate() // <-- Ensure migration runs
        return flyway
    }
}