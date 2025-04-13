package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.blogAdmin
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.util.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.ZonedDateTime

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class Admin(val ctx: Context,
            private val blogService: BlogService) : Base(ctx) {

    @GetMapping("/{segment}")
    fun allBlogPosts(
        @PathVariable segment: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, true)

        logger.info("allBlogPosts Fetch blog posts with: $blogParams")

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }

        if (segment == NEW_SEGMENT) {
            val blogForm = BlogForm()
            model.addAttribute("title",msg(ctx.messageSource,"newBLog"))
            model.addAttribute("blogForm", blogForm)
        }

        // Set blog related fields
        model.addAttribute("changed", blogParams.blog.changed)
        model.addAttribute("blogForm", blogParams.blog.toForm())
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
        logger.info("getting GUI with blogPosts")
        return "blogPosts"
    }

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
        return "redirect:$ADMIN_DIR/$firstSegment"
    }

    @PostMapping(value = ["/{segment}"])
    fun action(
        redirectAttributes: RedirectAttributes,
        @ModelAttribute blogForm: BlogForm,
        @PathVariable segment: String,
        action: String,
        blogId: Long?,
        changed: ZonedDateTime?,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        @AuthenticationPrincipal owner: Owner?
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        //TODO check and use objectName in code.
        logger.info("blog: path: $path action:  $action blogid: $blogId formName: ${bindingResult.objectName}")
        val blogOwner = owner?.user ?: run {
            // TODO is this the best action, alternative return error message in the current screen
            if (ctx.appConfig.auth)
                throw (ResponseStatusException(HttpStatus.UNAUTHORIZED, "not authorized for save action"))
            else blogAdmin
        }

        if (action == "save" || action == "saveCreate") {
            checkSegment(blogForm.segment, "segment", ctx.messageSource, bindingResult)
            checkStringSize(blogForm.subject, MAX_TITLE_SIZE,  "subject", bindingResult, ctx.messageSource, 1)
            checkStringSize(blogForm.about, MAX_SUMMARY_SIZE,  "about", bindingResult, ctx.messageSource)
            checkInt(blogForm.position,"position", bindingResult, ctx.messageSource, -1000,1000)
            if (bindingResult.hasErrors()) {
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }
            val langCode = (ctx.httpSession.getAttribute("langcode") as String?) ?:
                validProjectLanguageCode(LocaleContextHolder.getLocale().language)
            try {
                blogService.save(blogId, blogForm, langCode, blogOwner)
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException -> handleRecoverableError(e, "dbSave", bindingResult)
                    is DuplicateSegmentException ->
                        bindingResult.addFieldError("segment", "duplicate",  ctx.messageSource, blogForm.segment)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogPosts"
            }
            val newPath = "$ADMIN_DIR/${if (action == "save") blogForm.segment else NEW_SEGMENT}"
            return "redirect:$newPath"
        }
        if (action == "createPost") {
            return "redirect:$ADMIN_DIR/$segment/$NEW_SEGMENT"
        } else {
            try {
                blogService.delete(blogId, blogForm)
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
        logger.info("Prepare fetch blog posts with: $blogParams")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", ADMIN_DIR)
        model.addAttribute("changed", changed)
        logger.info("prepared)")
    }

    @PostMapping
    fun getBlogAdmin(
        result: String
    ): String {
        return "redirect:$ADMIN_DIR/${result}"
    }
}
