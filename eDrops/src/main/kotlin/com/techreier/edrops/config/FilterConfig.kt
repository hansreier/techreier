package com.techreier.edrops.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig {
    @Bean
    fun requestFilter(): FilterRegistrationBean<RequestFilter> {
        logger.info("Inside filterConfig")
        val registrationBean = FilterRegistrationBean<RequestFilter>()
        registrationBean.filter = RequestFilter()
        registrationBean.addUrlPatterns("/*")
        registrationBean.order = Int.MIN_VALUE
        return registrationBean
    }

}