package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val BLOG = "blogs"
const val BLOG_DIR = "/$BLOG"

@Controller
@RequestMapping(BLOG_DIR)
@Validated
class BlogController(private val dbService: DbService,
                     messageSource: MessageSource) :
                     BaseController(dbService, messageSource) {
    @GetMapping("/{segment}")
    fun allBlogTexts(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang")  langCode: String?,
        request: HttpServletRequest, model: Model
    ): String {
        val blogParams = setCommonModelParameters(BLOG, model, request, langCode, segment)
        if (blogParams.blogId < 0) {
            // If blog is not found, redirect to first blog, other alternatives in comment below
            // throw ResponseStatusException(HttpStatus.NOT_FOUND, BLOG)
            // return "redirect:/"
            logger.warn("Blog $segment is not found in language: ${blogParams.locale.language}")
            return "redirect:$BLOG_DIR/${fetchFirstBlog(blogParams.locale.language).segment}"
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.locale.language)
        model.addAttribute("blog", blog)
        return "blogSummaries"
    }

    // Redirect to first blog
    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        request: HttpServletRequest, model: Model
    ): String {
        val blogParams = setCommonModelParameters(BLOG, model, request, language)
        return "redirect:$BLOG_DIR/${fetchFirstBlog(blogParams.locale.language).segment}"
    }

    // Redirect to other blog from menu
    @PostMapping
    fun getBlog(redirectAttributes: RedirectAttributes, result: String): String {
        logger.info("Blog controller redirect: $result")
        return redirect(redirectAttributes, result, BLOG_DIR)
    }
}