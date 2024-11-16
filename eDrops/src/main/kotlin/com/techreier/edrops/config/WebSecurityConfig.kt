package com.techreier.edrops.config

import com.techreier.edrops.controllers.LOGIN_DIR
import com.techreier.edrops.service.UserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
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
                // authorize("/h2-console/**", permitAll)
                //  authorize("/robots.txt", permitAll)
                //  authorize("/favicon.ico", permitAll)
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
            csrf {
                ignoringRequestMatchers("/h2-console/**")
            }
            headers {
                frameOptions { sameOrigin = true } //Required for h2-console
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
    fun authenticationManager(userDetailsService: UserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationManager {
        return AuthenticationManager {
            val userDetails = userDetailsService.loadUserByUsername(it.name)
            if (passwordEncoder.matches(it.credentials.toString(), userDetails.password)) {
                UsernamePasswordAuthenticationToken(userDetails.username, userDetails.password, userDetails.authorities)
            } else {
                throw BadCredentialsException("Bad credentials for user: ${it.name}")
            }
        }
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
