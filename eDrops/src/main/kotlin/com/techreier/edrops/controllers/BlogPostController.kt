package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.util.checkId
import com.techreier.edrops.util.checkSegment
import com.techreier.edrops.util.checkStringSize
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.ZonedDateTime

@Controller
@RequestMapping(ADMIN_DIR)
class BlogPostController(
    ctx: Context,
    private val blogPostService: BlogPostService,
) : Base(ctx) {

    @GetMapping("/{segment}/{subsegment}")
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, true)

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        logger.info("allBlogPosts Fetch blog posts with: $blogParams")

        if (subsegment == NEW_SEGMENT) {
            val blogPostForm = BlogPostForm()
            model.addAttribute("blogPostForm", blogPostForm)
        } else {
            val selectedBlogPost = select(subsegment, blogParams.blog)
            if (selectedBlogPost == null) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }

            logger.info("getting GUI with blogPost. ${selectedBlogPost.title}")
            model.addAttribute("changed", selectedBlogPost.changed)
            model.addAttribute("blogPostForm", selectedBlogPost.toForm())
        }

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$ADMIN_DIR/$segment/")

        return "blogPosts"
    }

    @PostMapping(value = ["/{segment}/{subsegment}"])
    fun action(
        redirectAttributes: RedirectAttributes,
        @ModelAttribute blogPostForm: BlogPostForm,
        @PathVariable segment: String,
        @PathVariable subsegment: String?,
        action: String,
        blogId: Long?,
        changed: ZonedDateTime?,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blog Post: path: $path action:  $action blogid: $blogId")
        if (action == "blog") {
            return "redirect:$ADMIN_DIR/$segment"
        }
        if (action == "save" || action == "saveCreate") {
            checkId(blogId, bindingResult)
            if ((blogId != null) && checkSegment(blogPostForm.segment, "segment",  bindingResult)) {
                if (blogPostService.duplicate(blogPostForm.segment, blogId, blogPostForm.id)) {
                    bindingResult.rejectValue("segment", "error.duplicate", blogPostForm.segment)
                }
            }

            checkSegment(blogPostForm.segment,  "segment", bindingResult)
            checkStringSize(blogPostForm.title, MAX_TITLE_SIZE,  "title", bindingResult, 1)
            checkStringSize(blogPostForm.summary, MAX_SUMMARY_SIZE, "summary", bindingResult)
            if (bindingResult.hasErrors()) {
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }

            try {
                blogId?.let { blogPostService.save(it, blogPostForm)}
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }

            val newPath = "$ADMIN_DIR/$segment${if (action == "save") "/${blogPostForm.segment}" else "/$NEW_SEGMENT"}"
            return "redirect:$newPath"
        }
        if (action == "createPost") {
            return "redirect:$ADMIN_DIR/$segment/$NEW_SEGMENT"
        } else {
            try {
                blogPostService.delete(blogId, blogPostForm)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }
            return "redirect:$ADMIN_DIR/$segment"
        }
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String,
        changed: ZonedDateTime?,
    ) {
        val blogParams = fetchBlogParams(model, request, response, segment, true)
        logger.info("Prepare allBlogPosts Fetch blog posts with: $blogParams")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$ADMIN_DIR/$segment/")
        model.addAttribute("changed", changed)
        logger.info("prepared)")
    }

    private fun select(
        subsegment: String,
        blog: BlogDTO?,
    ) = blog?.blogPosts?.let { blogPosts ->
        var index: Int
        val no = subsegment.toIntOrNull() //Allow for number to be entered in path
        no?.let {
            index =
                if (it > blogPosts.size) {
                    blogPosts.size - 1
                } else if (it <= 0) {
                    0
                } else {
                    it - 1
                }
            blogPosts[index]
        } ?: blogPosts.find { it.segment == subsegment }
    }
}

