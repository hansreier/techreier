package com.techreier.edrops.config

import com.techreier.edrops.controllers.LOGIN_DIR
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler

@Configuration
@EnableWebSecurity
class WebSecurityConfig(val appConfig: AppConfig) {

    // Spring security is totally changed
    // Code style is very Kotlin spesific, DSL (Domain Specific Language)
    // No need to separately permit css or h2 console
    // Access is no longer denied by default
    //https://docs.spring.io/spring-security/reference/servlet/configuration/kotlin.html
    //https://codersee.com/spring-boot-3-spring-security-6-with-kotlin-jwt/
    //https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    //AccessDeniedException is connected with Authorization
    //AuthenticationException is connected with authorization (e.g. after login page)
    //https://www.baeldung.com/spring-security-exceptions
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            logger.info("Inside security config")
            authorizeRequests {
                authorize("/admin/**", authenticated)
                authorize("/**", permitAll)
                //  authorize("/css/*", permitAll)
                //  authorize("/h2-console/**", denyAll)
                //  authorize("/robots.txt", permitAll)
                //  authorize("/images/favicon.ico, permitall)
            }
            formLogin {
                loginPage ="/login"
                defaultSuccessUrl("/", true)
                failureUrl = "/login"
                authenticationFailureHandler = authFailureHandler()
            }
            logout {
                logoutSuccessUrl = "/login"
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
    fun userDetailService(): UserDetailsService {
        val userDetailsService = InMemoryUserDetailsManager()
        val user = User.withUsername(appConfig.user) //Simple builder to define user
            .password(appConfig.password)
            .authorities("read","write")
            .build()

        userDetailsService.createUser(user)
        return userDetailsService
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authFailureHandler(): AuthenticationFailureHandler {
        return AuthFailureHandler(LOGIN_DIR)
    }


}
