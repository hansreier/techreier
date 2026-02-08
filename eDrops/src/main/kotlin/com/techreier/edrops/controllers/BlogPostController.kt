package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.util.msg
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping(BLOG_DIR)
@Validated
class BlogPostController(private val ctx : Context,
                         private val blogPostService: BlogPostService
) : BaseController(ctx) {

    @GetMapping("/{segment}/{subsegment}")
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, false, false)

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        val (blogPost, blogText) = blogPostService.readBlogPost(blogParams.blog.id, subsegment, false)
        if (blogPost == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }

        logger.info("Fetch blog post (including text): $blogPost.segment")
        val datePattern = msg(ctx.messageSource, "format.datetime")
        val blogPostDto = blogPost.toDTO(timeZone(), datePattern, markdown,true, blogText)
        model.addAttribute("blogPost", blogPostDto)
        return "blogPost"
    }

}
