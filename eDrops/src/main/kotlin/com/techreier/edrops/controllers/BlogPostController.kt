package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
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
    context: Context,
    private val blogPostService: BlogPostService,
) : Base(context) {

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
            val blogPostForm = BlogPostForm(null,"","","")
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
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")

        return "blogPosts"
    }

    // TODO Removed one of the paths due to collision. Consequence?
    //  @PostMapping(value = ["/{segment}/{subsegment}", "/{segment}"])
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
        model: Model,
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blog Post: path: $path action:  $action blogid: $blogId")
        if (action == "save" || action == "saveCreate") {
            checkSegment(blogPostForm.segment, "blogPostForm", "segment", bindingResult)
            checkStringSize(blogPostForm.title, MAX_TITLE_SIZE, "blogPostForm", "title", bindingResult, 1)
            checkStringSize(blogPostForm.summary, MAX_SUMMARY_SIZE, "blogPostForm", "summary", bindingResult)
            if (bindingResult.hasErrors()) {
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }

            try {
                blogPostService.save(blogId, blogPostForm)
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    is DuplicateSegmentException ->
                        bindingResult.addFieldError("blogPostForm", "segment", "duplicate", blogPostForm.segment)

                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }

            val newPath = "$ADMIN_DIR/$segment${if (action == "save") "/${blogPostForm.segment}" else "/$NEW_SEGMENT"}"
            return "redirect:$newPath"
        }
        if (action == "create") {
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
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
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

