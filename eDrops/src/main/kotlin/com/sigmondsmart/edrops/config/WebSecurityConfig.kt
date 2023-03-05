package com.sigmondsmart.edrops.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .httpBasic().disable()
            .authorizeRequests()
            .antMatchers("/**","/css/*").permitAll()
            .antMatchers("/h2-console/**").permitAll()
            .anyRequest().authenticated()
        http.csrf().disable().headers().frameOptions().disable() //needed to enable H2 console
        return http.build()
    }
}