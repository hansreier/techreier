package com.techreier.edrops.config

import com.techreier.edrops.controllers.LOGIN_DIR
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    // Spring security is totally changed from Spring Boot v3
    // Code style is very Kotlin specific, DSL (Domain Specific Language)
    // No need to separately permit css or h2 console
    // Access is no longer denied by default
    // https://docs.spring.io/spring-security/reference/servlet/configuration/kotlin.html
    // https://codersee.com/spring-boot-3-spring-security-6-with-kotlin-jwt/
    // https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    // https://www.baeldung.com/spring-security-exceptions
    @Bean
    fun filterChain(
        http: HttpSecurity,
        appConfig: AppConfig,
    ): SecurityFilterChain {
        http {
            logger.info("Inside security config")
            authorizeHttpRequests {
                if (appConfig.auth) {
                    authorize("/admin/**", authenticated)
                }
                authorize("/**", permitAll)
                //  authorize("/css/*", permitAll)
                // authorize("/h2-console/**", permitAll)
                //  authorize("/robots.txt", permitAll)
                //  authorize("/favicon.ico", permitAll)
            }
            formLogin {
                loginPage = "/login"
                defaultSuccessUrl("/", true)
                failureUrl = "/login"
                authenticationFailureHandler = authFailureHandler()
            }
            logout {
                logoutSuccessUrl = "/login"
            }
            csrf {
                ignoringRequestMatchers("/h2-console/**")
            }
            headers {
                frameOptions { sameOrigin = true } // Required for h2-console
            }

            sessionManagement {
                invalidSessionUrl = "/"
                sessionConcurrency {
                    maximumSessions = 1
                    expiredUrl = "/"
                }
            }
        }
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authFailureHandler(): AuthenticationFailureHandler = AuthFailureHandler(LOGIN_DIR)
}
