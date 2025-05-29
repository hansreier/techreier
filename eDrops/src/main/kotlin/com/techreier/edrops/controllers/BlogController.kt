package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val BLOG = "blogs"
const val BLOG_DIR = "/$BLOG"

@Controller
@RequestMapping(BLOG_DIR)
@Validated
class BlogController(context: Context) : BaseController(context) {

    @GetMapping("/{segment}")
    fun allBlogTexts(
        @PathVariable segment: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, true, false)
        if (blogParams.blog == null) {
            logger.warn("Blog $segment is not found in language: ${blogParams.usedLangCode}")

            val firstSegment = readFirstSegment(blogParams.usedLangCode)
            return if (firstSegment != null) {
                redirectAttributes.addFlashAttribute("warning", "readFirstBlog")
                "redirect:$BLOG_DIR/$firstSegment"
            } else {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                "redirect:/$HOME_DIR"
            }
        }
        logger.info("allBlogPosts Fetch blog posts with: $blogParams and summary")
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
        val firstSegment = readFirstSegment(blogParams.usedLangCode)
        if (firstSegment == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        return "redirect:$BLOG_DIR/$firstSegment"
    }

}
