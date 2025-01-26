package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.ZonedDateTime

@Controller
@RequestMapping(ADMIN_DIR)
class BlogEntryController(
    private val dbService: DbService,
    messageSource: MessageSource
) : BaseController(dbService, messageSource) {

    @GetMapping("/{segment}/{subsegment}")
    fun blogEntry(
        @PathVariable segment: String, @PathVariable subsegment: String,
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest, model: Model
    ): String {
        val blogParams = setCommonModelParameters(ADMIN, model, request, langCode, segment)
        if (blogParams.blogId < 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN)
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.locale.language)
        val selectedBlogEntry = blog?.blogEntries?.let { blogEntries ->
            var index: Int
            val no = subsegment.toIntOrNull()
            no?.let {
                index = if (it > blogEntries.size) {
                    blogEntries.size - 1
                } else if (it <= 0) 0 else it - 1
                blogEntries[index]
            } ?: blogEntries.find { it.segment == subsegment }
        } ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN)

        model.addAttribute("blog", blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/${segment}/")

        logger.info("getting GUI with blogEntry. ${selectedBlogEntry.title}")
        val blogEntryForm = BlogEntryForm(
            selectedBlogEntry.id, selectedBlogEntry.segment, selectedBlogEntry.title,
            selectedBlogEntry.summary
        )
        model.addAttribute("changed", selectedBlogEntry.changed)
        model.addAttribute("blogEntryForm", blogEntryForm)
        return "blogEntries"
    }

    @PostMapping(value = ["/{segment}/{subsegment}","/{segment}"])
    fun action(
        redirectAttributes: RedirectAttributes,
        @ModelAttribute blogEntryForm: BlogEntryForm,
        @PathVariable segment: String,
        @PathVariable subsegment: String?,
        action: String,
        blogId: Long?,
        changed: ZonedDateTime?,
        bindingResult: BindingResult,
        request: HttpServletRequest,
        model: Model
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blog entry: path: $path action:  $action")
        if (action == "save" || action == "saveCreate") {
            checkSegment(blogEntryForm.segment, "blogEntryForm", "segment", bindingResult)
            checkStringSize(blogEntryForm.title, MAX_TITLE_SIZE, "blogEntryForm", "title", bindingResult)
            checkStringSize(blogEntryForm.summary, MAX_SUMMARY_SIZE, "blogEntryForm", "summary", bindingResult)
            if (bindingResult.hasErrors()) {
                prepare(model, request, segment, changed)
                return "blogEntries"
            }

            try {
                dbService.saveBlogEntry(blogId, blogEntryForm)
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    is DuplicateSegmentException ->
                        bindingResult.addFieldError("blogEntryForm", "segment", "duplicate", blogEntryForm.segment)

                    else -> throw e
                }
                prepare(model, request, segment, changed)
                return "blogEntries"
            }

            val newPath = "$ADMIN_DIR/$segment${if (action == "save") "/${blogEntryForm.segment}" else ""}"
            return "redirect:$newPath"
        }
        if (action == "create") {
            return "redirect:$ADMIN_DIR/$segment"
        } else {
            try {
                dbService.deleteBlogEntry(blogId, blogEntryForm)
            } catch (e: DataAccessException) {
                handleRecoverableError(e, "dbDelete", bindingResult)
                prepare(model, request, segment, changed)
                return "blogEntries"
            }
            return "redirect:$ADMIN_DIR/$segment"
        }
    }

    private fun prepare(model: Model, request: HttpServletRequest, segment: String, changed: ZonedDateTime?) {
        val blogParams = setCommonModelParameters(ADMIN, model, request, null, segment)
        logger.info("Prepare allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.locale.language)
        model.addAttribute("blog", blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/${segment}/")
        model.addAttribute("changed", changed)
        logger.info("prepared)")
    }
}