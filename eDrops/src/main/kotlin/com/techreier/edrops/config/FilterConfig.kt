package com.techreier.edrops.config

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("NotUsedProfile")
class FilterConfig {
    //TODO filterconfig and filter defined for future use
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