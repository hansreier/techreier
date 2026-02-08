package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogPostService
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogPostForm
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.checkSegment
import com.techreier.edrops.util.checkStringSize
import com.techreier.edrops.util.msg
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping(BLOG_EDIT_DIR)
class BlogPostEditController(
    private val ctx: Context,
    private val blogPostService: BlogPostService,
) : BaseController(ctx) {

    @GetMapping("/{segment}/{subsegment}")
    fun blogPost(
        @PathVariable segment: String,
        @PathVariable subsegment: String,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, false, true)

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }
        logger.info("Fetch blog posts: $blogParams")

        model.addAttribute("postStates", PostState.entries)
        if (subsegment == NEW_SEGMENT) {
            val blogPostForm = BlogPostForm()
            model.addAttribute("blogPostForm", blogPostForm)
            model.addAttribute("postHeadline", msg(ctx.messageSource, "newPost"))
        } else {
            val (blogPost, blogText) = blogPostService.readBlogPost(blogParams.blog.id, subsegment, true)
            if (blogPost == null) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            }

            val datePattern = msg(ctx.messageSource, "format.datetime")
            val blogPostDto = blogPost.toDTO(timeZone(), datePattern, markdown,false, blogText)

            logger.info("getting GUI with blogPost. ${blogPost.title}")
            val contentChanged = blogPostDto.blogText?.changedString ?: ""
            model.addAttribute("postHeadline", blogPostDto.title)
            model.addAttribute("created", (blogPostDto.createdString))
            model.addAttribute("changed", (blogPostDto.changedString))
            model.addAttribute("contentChanged", contentChanged)
            model.addAttribute("blogPostForm", blogPostDto.toForm())
        }

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$BLOG_EDIT_DIR/$segment/")

        return "blogPostEdit"
    }

    @PostMapping(value = ["/{segment}/{subsegment}"])
    fun action(
        redirectAttributes: RedirectAttributes,
        @ModelAttribute form: BlogPostForm,
        @PathVariable segment: String,
        @PathVariable subsegment: String?,
        action: String,
        blogId: Long?,
        changed: String,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blog Post: path: $path action:  $action blogid: $blogId")
        if (action == "blog") {
            return "redirect:$BLOG_EDIT_DIR/$segment"
        }
        if (action == "save" || action == "create") {

            blogId ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "BlogId is missing, probably programming error"
            )

            if (checkSegment(form.segment, "segment", bindingResult)) {
                if (blogPostService.duplicate(form.segment, blogId, form.state, form.id)) {
                    bindingResult.rejectValue("segment", "error.duplicate", form.segment)
                }
            }

            checkStringSize(form.title, MAX_TITLE_SIZE, "title", bindingResult, 1)
            form.title = form.title.replaceFirstChar { it.uppercaseChar() }
            checkStringSize(form.summary, MAX_SUMMARY_SIZE, "summary", bindingResult)

            if (bindingResult.hasErrors()) {
                bindingResult.reject("error.savePost")
                prepare(model, request, response, segment, changed)
                return "blogPostEdit"
            }

            try {
                blogPostService.save(blogId, form, now())
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogPostEdit"
            }

            val newPath =
                "$BLOG_EDIT_DIR/$segment${if (action == "save") "/${form.segment}" else "/$NEW_SEGMENT"}"
            return "redirect:$newPath"
        }

        if (action == "delete") { //TODO evaluate if should stay on this page if more posts left, a bit work
            try {
                blogPostService.delete(blogId, form)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, changed)
                return "blogPostEdit"
            }
            return "redirect:$BLOG_EDIT_DIR/$segment"
        }

        if (action == "view") {
            if ((form.focus.isNotEmpty() || form.preview.isEmpty())) {
                if (!form.summary.isBlank() && (form.focus.isEmpty() || form.focus.equals("s"))) {
                    val summary = Markdown().toHtml(form.summary, true)
                    model.addAttribute("summary", summary)
                    form.preview = "x"
                }

                if (!form.content.isBlank() && (form.focus.isEmpty() || form.focus.equals("c"))) {
                    val content = Markdown().toHtml(form.content, true)
                    model.addAttribute("content", content)
                    form.preview = "x"
                }
            } else {
                form.preview = ""
            }
            prepare(model, request, response, segment, changed)
            return "blogPostEdit"
        }

        // This should never really occur
        bindingResult.reject("error.illegalAction")
        prepare(model, request, response, segment, changed)
        return "blogPostEdit"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String,
        changed: String,
    ) {
        val blogParams = fetchBlogParams(model, request, response, segment, true)
        logger.info("Prepare allBlogPosts Fetch blog posts with: $blogParams")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$BLOG_EDIT_DIR/$segment/")
        model.addAttribute("changed", changed)
        model.addAttribute("postStates", PostState.entries)
        logger.info("prepared)")
    }

}

