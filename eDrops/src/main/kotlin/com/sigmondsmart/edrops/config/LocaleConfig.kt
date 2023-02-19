package com.sigmondsmart.edrops.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*


//https://www.baeldung.com/spring-boot-internationalization
//https://reflectoring.io/spring-boot-internationalization/
@Configuration
class LocaleConfig : WebMvcConfigurer {

    @Bean
    fun localeResolver(): LocaleResolver {
        val slr = SessionLocaleResolver()
        slr.setDefaultLocale(Locale.forLanguageTag("nb-NO"))
       // slr.setDefaultLocale(Locale.UK)
        return slr
    }

    //Switch to a new locale based on the value of the lang parameter appended to HTtP request URL
    //http://localhost:8080/index?language=de
    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val lci = LocaleChangeInterceptor()
        lci.paramName = "lang"
        return lci
    }

    //Add bean to Interceptor Registry
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
    }
}