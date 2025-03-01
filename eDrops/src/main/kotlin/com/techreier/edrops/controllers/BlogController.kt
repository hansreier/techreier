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
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, true)
        if (blogParams.blog == null) {
            logger.warn("Blog $segment is not found in language: ${blogParams.locale.language}")
            val firstSegment = readFirstSegment(blogParams.locale.language)
            if (firstSegment == null) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }
            return "redirect:$BLOG_DIR/$firstSegment"
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams and summary")
        model.addAttribute("blog", blogParams.blog)
        return "blogSummaries"
    }

    // Redirect to first blog
    @GetMapping
    fun redirect(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val blogParams = fetchBlogParams(model, request, response)
        val firstSegment = readFirstSegment(blogParams.locale.language)
        if (firstSegment == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return "redirect:$BLOG_DIR/$firstSegment"
    }

    // Redirect to other blog from menu
    @PostMapping
    fun getBlog(
        result: String,
    ): String {
        return "redirect:$BLOG_DIR/${result}"
    }
}
