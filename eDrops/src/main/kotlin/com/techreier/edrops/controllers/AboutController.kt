package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.service.BlogService
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.getDocIndex
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.i18n.SessionLocaleResolver

const val ABOUT = "about"
const val ABOUT_DIR = "/$ABOUT"

@Controller
@RequestMapping(ABOUT_DIR)
class AboutController(
    blogService: BlogService,
    dbService: DbService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : BaseController(blogService, dbService, messageSource, sessionLocaleResolver, appConfig) {
    @GetMapping("/{segment}")
    fun content(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, langCode)
        val docIndex = getDocIndex(about, blogParams.locale.language, segment)
        val doc = about[docIndex]

        val docText: String = markdownToHtml(doc, ABOUT_DIR)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        return ABOUT
    }

    // Redirect to page with segment in path
    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, language)
        val docIndex = getDocIndex(about, blogParams.locale.language)
        val doc = about[docIndex]
        return "redirect:$ABOUT_DIR/${doc.segment}"
    }

    @PostMapping
    fun getEntry(doc: String): String {
        logger.info("About controller redirect")
        return "redirect:$ABOUT_DIR/$doc"
    }
}
