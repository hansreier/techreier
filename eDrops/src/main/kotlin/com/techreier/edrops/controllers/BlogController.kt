package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.GenService
import com.techreier.edrops.util.addFlashAttributes
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val BLOG = "blogs"
const val BLOG_DIR = "/$BLOG"

@Controller
@RequestMapping(BLOG_DIR)
@Validated
class Blog(
    blogService: BlogService,
    genService: GenService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : Base(blogService, genService, messageSource, sessionLocaleResolver, appConfig) {
    @GetMapping("/{segment}")
    fun allBlogTexts(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        @RequestParam(required = false, name = "topic") topicKey: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, topicKey, langCode, segment, true)
        if (blogParams.blog == null) {
            logger.warn("Blog $segment is not found in language: ${blogParams.locale.language}")
            return "redirect:$BLOG_DIR/${readFirstSegment(blogParams.locale.language)}"
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        model.addAttribute("blog", blogParams.blog)
        return "blogSummaries"
    }

    // Redirect to first blog
    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        @RequestParam(required = false, name = "topic") topicKey: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, topicKey, language)
        return "redirect:$BLOG_DIR/${readFirstSegment(blogParams.locale.language)}"
    }

    // Redirect to other blog from menu
    @PostMapping
    fun getBlog(
        redirectAttributes: RedirectAttributes,
        topicKey: String,
        result: String,
    ): String {
        logger.info("Blog controller redirect: $result")
        redirectAttributes.addFlashAttributes(topicKey)
        return redirect(redirectAttributes, result, BLOG_DIR)
    }
}
