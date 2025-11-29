package com.techreier.edrops.config

import org.h2.server.web.JakartaWebServlet
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

// To be able to view the h2 console. It is not possible just to define properties in yaml. Spring Boot bug?
@Configuration
@Profile("h2")
class H2ConsoleConfig {

    @Bean
    fun h2ConsoleServletRegistration(): ServletRegistrationBean<JakartaWebServlet> { // <-- Updated type parameter
        val registration = ServletRegistrationBean(JakartaWebServlet()) // <-- Updated instantiation

        registration.addUrlMappings("/h2-console/*") // Access via http://localhost:8080/console/
        registration.addInitParameter("webAllowOthers", "true")
        registration.setName("h2-console")
        registration.setLoadOnStartup(1)

        return registration
    }
}