package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.USE_COMMONMARK
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.GenService
import com.techreier.edrops.dbservice.InitService
import com.techreier.edrops.util.MarkdownC
import com.techreier.edrops.util.MarkdownF
import com.techreier.edrops.util.MarkdownBase
import jakarta.servlet.http.HttpSession
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.servlet.i18n.SessionLocaleResolver

@Component
data class Context(
    val initService: InitService,
    val blogService: BlogService,
    val genService: GenService,
    val messageSource: MessageSource,
    val sessionLocaleResolver: SessionLocaleResolver,
    val appConfig: AppConfig,
    val httpSession: HttpSession,
    val markdown: MarkdownBase = if (USE_COMMONMARK) MarkdownC() else MarkdownF()
)
