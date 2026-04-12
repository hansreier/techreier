package com.techreier.edrops.controllers

import com.techreier.edrops.config.*
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.exceptions.BlogNotFoundException
import com.techreier.edrops.exceptions.TopicNotFoundException
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.util.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.dao.DataAccessException
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val EDIT = "edit"
const val BLOG_EDIT_DIR = "/$EDIT"

@Controller
@RequestMapping(BLOG_EDIT_DIR)
class BlogEditController(
    val ctx: Context,
    private val blogService: BlogService,
) : BaseController(ctx) {


    @GetMapping("/{segment}","/")
    fun allBlogPosts(
        @PathVariable segment: String = "",
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
        @AuthenticationPrincipal owner: Owner?,
    ): String {
        authorize(owner)
        val blogParams = fetchBlogParams(model, request, response, segment, true, true)

        logger.info("Fetch blog posts: $blogParams")

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }

        if (segment == NEW_SEGMENT) {
            model.addAttribute("title", msg(ctx.messageSource, "newBLog"))
        }

        // Set blog related fields
        model.addAttribute("changed", blogParams.blog.changedText)
        model.addAttribute("blogForm", blogParams.blog.toForm())
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("blogPath", "$BLOG_EDIT_DIR/$segment/")
        logger.info("getting GUI with blogPosts")
        return "blogEdit"
    }

    @PostMapping(value = ["/{segment}"])
    fun action(
        redirectAttributes: RedirectAttributes,
        @ModelAttribute form: BlogForm,
        @PathVariable segment: String,
        action: String,
        changed: String,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        @AuthenticationPrincipal owner: Owner?,
    ): String {

        val blogPrincipal = authorize(owner, segment)
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)

        logger.info("blog: path: $path action=$action blogid=${blogPrincipal.blogId}")
        if (action == "back")  {
            logger.info("ReierAsk going back")
            return "redirect:/$HOME_DIR"
        }
        if (action == "save" || action == "create" || action == "createPost") {
            checkStringSize(form.subject, MAX_TITLE_SIZE, "subject", bindingResult, 1)
            form.subject = form.subject.replaceFirstChar { it.uppercaseChar() }
            checkStringSize(form.about, MAX_SUMMARY_SIZE, "about", bindingResult)
            checkInt(form.position, "position", bindingResult, -1000, 1000)

            if (checkSegment(form.segment, "segment", bindingResult)) {
                if (blogService.duplicate(form.segment, blogPrincipal)) {
                    bindingResult.rejectValue("segment", "error.duplicate", form.segment)
                }
            }

            if (bindingResult.hasErrors()) {
                bindingResult.reject("error.saveBlog")
                prepare(model, request, response, segment, changed)
                return "blogEdit"
            }
            try {
                blogService.save(blogPrincipal, form, now())
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException -> handleRecoverableError(e, "dbSave", bindingResult)
                    is TopicNotFoundException -> handleRecoverableError(e, "topicNotFound", bindingResult)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogEdit"
            }
            if (action == "createPost") {
                return "redirect:$BLOG_EDIT_DIR/$segment/$NEW_SUBSEGMENT/${PostState.IDEA.lower()}"
            }
            val newPath = "$BLOG_EDIT_DIR/${if (action == "save") form.segment else NEW_SEGMENT}"
            return "redirect:$newPath"
        }

        if (action == "delete") {
            if (form.postLock) {
                bindingResult.reject("error.locked")
                prepare(model, request, response, segment, changed)
                return "blogEdit"
            }
            try {
                blogService.delete(blogPrincipal.blogId)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, changed)
                return "blogEdit"
            }
            return "redirect:/$HOME_DIR"
        }
        if (action == "view") {
            if (form.preview.isEmpty()) {
                if (!form.about.isBlank()) {
                    val about = Markdown().toHtml(form.about)
                    model.addAttribute("about", about)
                    form.preview = "x"
                }
            } else {
                form.preview = ""
            }
            prepare(model, request, response, segment, changed)
            return "blogEdit"
        }
        // This should never really occur
        logger.error("Illegal action: $action")
        bindingResult.reject("error.illegalAction")
        prepare(model, request, response, segment, changed)

        return "blogEdit"
    }

    private fun prepare(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String,
        changed: String,
    ) {
        val blogParams = fetchBlogParams(model, request, response, segment, true, true)

        logger.info("Prepare fetch blog posts with: $blogParams")
        blogParams.blog ?: throw BlogNotFoundException("Blog with segment $segment not found")
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("blogPath", "$BLOG_EDIT_DIR/$segment/")
        model.addAttribute("changed", changed)
        logger.info("prepared")
    }

}
