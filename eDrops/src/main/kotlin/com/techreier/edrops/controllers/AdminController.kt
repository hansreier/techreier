package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.GenService
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.util.addFlashAttributes
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class Admin(
    blogService: BlogService,
    genService: GenService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : Base(blogService, genService, messageSource, sessionLocaleResolver, appConfig) {
    @GetMapping("/{segment}")
    fun allBlogEntries(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        @RequestParam(required = false, name = "topic") topicKey: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, topicKey, langCode, segment, true)
        if (blogParams.blog == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN)
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        if (blogParams.action == "create" || blogParams.action == "saveCreate") {
            logger.info("getting GUI with new blogEntry")
            val blogEntryForm = BlogEntryForm(null, "", "", "")
            model.addAttribute("changed", null)
            model.addAttribute("blogEntryForm", blogEntryForm)
        }
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        @RequestParam(required = false, name = "topic") topicKey: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, topicKey, language)
        return "redirect:$ADMIN_DIR/${readFirstSegment(blogParams.locale.language)}"
    }

    @PostMapping
    fun getBlogAdmin(
        redirectAttributes: RedirectAttributes,
        result: String,
        topicKey: String
    ): String {
        logger.info("Admin controller redirect: $result")
        redirectAttributes.addFlashAttributes(topicKey)
        return redirect(redirectAttributes, result, ADMIN_DIR)
    }
}
