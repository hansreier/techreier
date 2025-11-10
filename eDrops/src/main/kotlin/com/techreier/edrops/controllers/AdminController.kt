package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.InitService
import com.techreier.edrops.domain.Owner
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

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class AdminController(val ctx: Context,
            private val blogService: BlogService, private val initService: InitService
) : BaseController(ctx) {

    @GetMapping("/{segment}")
    fun allBlogPosts(
        @PathVariable segment: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        redirectAttributes: RedirectAttributes,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, segment, true, true)

        logger.info("Fetch blog posts: $blogParams")

        if (blogParams.blog == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }

        if (segment == NEW_SEGMENT) {
            model.addAttribute("title",msg(ctx.messageSource,"newBLog"))
        }

        // Set blog related fields
        model.addAttribute("changed", blogParams.blog.changedText)
        model.addAttribute("blogForm", blogParams.blog.toForm())
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$ADMIN_DIR/$segment/")
        logger.info("getting GUI with blogPosts")
        return "blogAdmin"
    }

    @GetMapping
    fun redirect(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes
    ): String {
        val blogParams = fetchBlogParams(model, request, response, null, false, true)
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
        changed: String,
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
        val blogOwner = owner?.user ?:
            // TODO is this the best action, alternative return error message in the current screen
            if (ctx.appConfig.auth)
                throw (ResponseStatusException(HttpStatus.UNAUTHORIZED, "not authorized for save action"))
            else initService.blogAdmin

        blogOwner.id?: throw (ResponseStatusException(HttpStatus.UNAUTHORIZED, "No blogOwner exists"))

        if (action == "save" || action == "create" || action == "createPost") {
            val langCode = (ctx.httpSession.getAttribute("langcode") as String?) ?:
                getValidProjectLanguageCode(LocaleContextHolder.getLocale().language)
            if (checkSegment(blogForm.segment, "segment",  bindingResult)) {
                if (blogService.duplicate(blogForm.segment, blogOwner.id, langCode, blogId)) {
                        bindingResult.rejectValue("segment","error.duplicate", blogForm.segment)
                }
            }
            checkStringSize(blogForm.subject, MAX_TITLE_SIZE,  "subject", bindingResult,  1)
            blogForm.subject = blogForm.subject.replaceFirstChar { it.uppercaseChar() }
            checkStringSize(blogForm.about, MAX_SUMMARY_SIZE,  "about", bindingResult)
            checkInt(blogForm.position,"position", bindingResult,  -1000,1000)
            if (bindingResult.hasErrors()) {
                bindingResult.reject("error.saveBlog")
                prepare(model, request, response, segment, changed)
                return "blogAdmin"
            }
            try {
                blogService.save(blogId, blogForm, langCode, blogOwner, now())
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException -> handleRecoverableError(e, "dbSave", bindingResult)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogAdmin"
            }
            if (action == "createPost") {
                return "redirect:$ADMIN_DIR/$segment/$NEW_SEGMENT"
            }
            val newPath = "$ADMIN_DIR/${if (action == "save") blogForm.segment else NEW_SEGMENT}"
            return "redirect:$newPath"
        }

        if (action =="delete") {
            if (blogForm.postLock) {
                bindingResult.reject("error.locked")
                prepare(model, request, response, segment, changed)
                return "blogAdmin"
            }
            try {
                blogService.delete(blogId, blogForm)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, changed)
                return "blogAdmin"
            }
            return "redirect:/$HOME_DIR"
        }
        // This should never really occur
        bindingResult.reject("error.illegalAction")
        prepare(model, request, response, segment, changed)
        return "blogAdmin"
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
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("postPath", "$ADMIN_DIR/$segment/")
        model.addAttribute("changed", changed)
        logger.info("prepared)")
    }

}
