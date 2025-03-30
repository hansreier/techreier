package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.forms.BlogForm
import com.techreier.edrops.util.validProjectLanguageCode
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.ZonedDateTime

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class Admin(val context: Context, private val blogService: BlogService) : Base(context) {

    @GetMapping("/{segment}")
    fun allBlogEntries(
        @PathVariable segment: String?,
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
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        if (blogParams.action == "create" || blogParams.action == "saveCreate") {
            logger.info("getting GUI with new blogEntry")
            val blogEntryForm = BlogEntryForm(null, "", "", "")
            model.addAttribute("changed", null)
            model.addAttribute("blogEntryForm", blogEntryForm)
        }

        // Set blog related fields
        model.addAttribute("changed", blogParams.blog.changed)
        model.addAttribute("blogForm", blogParams.blog.toForm())
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
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
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blog: path: $path action:  $action blogid: $blogId")
        if (action == "save" || action == "saveCreate") {
            checkSegment(blogForm.segment, "blogForm", "segment", bindingResult)
            checkStringSize(blogForm.subject, MAX_TITLE_SIZE, "blogForm", "subject", bindingResult, 1)
            checkStringSize(blogForm.about, MAX_SUMMARY_SIZE, "blogForm", "about", bindingResult)
            if (bindingResult.hasErrors()) {
                //TODO field subject er tomt. Hvorfor.
                prepare(model, request, response, segment, changed)
                return "blogEntries"
            }
            val langCode = (context.httpSession.getAttribute("langcode") as String?) ?:
                validProjectLanguageCode(LocaleContextHolder.getLocale().language)
            try {
                blogService.save(blogId, blogForm, langCode)
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException -> handleRecoverableError(e, "dbSave", bindingResult)
                    is DuplicateSegmentException ->
                        bindingResult.addFieldError("blogEntryForm", "segment", "duplicate", blogForm.segment)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogEntries"
            }

            val newPath = "$ADMIN_DIR/$segment${if (action == "save") "/${blogForm.segment}" else ""}"
            return "redirect:$newPath"
        }
        if (action == "create") {
            return "redirect:$ADMIN_DIR/$segment"
        } else {
            try {
                blogService.delete(blogId, blogForm)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, response, segment, changed)
                return "blogEntries"
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
        logger.info("Prepare fetch blog entries with: $blogParams")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", ADMIN_DIR)
        model.addAttribute("changed", changed)
        logger.info("prepared)")
    }

    //TODO never used, why?
    private fun select(
        subsegment: String,
        blog: BlogDTO?,
    ) = blog?.blogEntries?.let { blogEntries ->
        var index: Int
        val no = subsegment.toIntOrNull()
        no?.let {
            index =
                if (it > blogEntries.size) {
                    blogEntries.size - 1
                } else if (it <= 0) {
                    0
                } else {
                    it - 1
                }
            blogEntries[index]
        } ?: blogEntries.find { it.segment == subsegment }
    }


    @PostMapping
    fun getBlogAdmin(
        result: String
    ): String {
        return "redirect:$ADMIN_DIR/${result}"
    }
}
