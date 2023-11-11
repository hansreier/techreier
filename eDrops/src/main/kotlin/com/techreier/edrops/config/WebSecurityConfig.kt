package com.techreier.edrops.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    // Spring security is totally changed
    // Code style is very Kotlin spesific, DSL (Domain Specific Language)
    // No need to separately permit css or h2 console
    // Access is no longer denied by default
    //https://docs.spring.io/spring-security/reference/servlet/configuration/kotlin.html
    //https://codersee.com/spring-boot-3-spring-security-6-with-kotlin-jwt/
    //https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            logger.info("Inside security config")
            authorizeRequests {
                authorize("/**", permitAll)
                authorize("/css/*", permitAll)
                //  authorize("/h2-console/**", denyAll)
                authorize( anyRequest, authenticated)
            }
        }
        return http.build()
    }
}
