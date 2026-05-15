package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.USE_COMMONMARK
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.GenService
import com.techreier.edrops.dbservice.InitService
import com.techreier.edrops.util.CMarkdown
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.MarkdownEngine
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
    val markdown: MarkdownEngine = if (USE_COMMONMARK) CMarkdown() else Markdown()
)
