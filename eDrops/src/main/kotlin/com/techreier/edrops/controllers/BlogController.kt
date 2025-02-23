package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
class Blog(context: Context) : Base(context) {

    @GetMapping("/{segment}")
    fun allBlogTexts(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, langCode, segment, true)
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, language)
        return "redirect:$BLOG_DIR/${readFirstSegment(blogParams.locale.language)}"
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
