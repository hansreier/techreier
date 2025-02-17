package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
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
class BlogController(
    private val dbService: DbService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : BaseController(dbService, messageSource, sessionLocaleResolver, appConfig) {
    @GetMapping("/{segment}")
    fun allBlogTexts(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = setCommonModelParameters(model, request, response, langCode, segment)
        if (blogParams.blog == null) {
            // If blog is not found, redirect to first blog, other alternatives in comment below
            // throw ResponseStatusException(HttpStatus.NOT_FOUND, BLOG)
            // return "redirect:/"
            logger.warn("Blog $segment is not found in language: ${blogParams.locale.language}")
            return "redirect:$BLOG_DIR/${fetchFirstBlog(blogParams.locale.language).segment}"
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        // TODO check that the same blog is not read twice, here and in setCommonModelParameters
      //  val blog = dbService.readBlogWithSameLanguage
//val blog = dbService.readBlog(blogParams.blogId)
        model.addAttribute("blog", blogParams.blog)
        return "blogSummaries"
    }

    // Redirect to first blog
    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        request: HttpServletRequest, response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = setCommonModelParameters(model, request, response, language)
        return "redirect:$BLOG_DIR/${fetchFirstBlog(blogParams.locale.language).segment}"
    }

    // Redirect to other blog from menu
    @PostMapping
    fun getBlog(
        redirectAttributes: RedirectAttributes,
        result: String,
    ): String {
        logger.info("Blog controller redirect: $result")
        return redirect(redirectAttributes, result, BLOG_DIR)
    }
}
