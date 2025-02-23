package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.GenService
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.servlet.i18n.SessionLocaleResolver

@Component
data class Params(
    val blogService: BlogService,
    val genService: GenService,
    val messageSource: MessageSource,
    val sessionLocaleResolver: SessionLocaleResolver,
    val appConfig: AppConfig
)
