package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.dbservice.BlogEntryService
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogEntryForm
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
class BlogEntryController(
    context: Context,
    private val blogEntryService: BlogEntryService,
) : Base(context) {

    @GetMapping("/{segment}/{subsegment}")
    fun blogEntry(
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
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")

        val selectedBlogEntry = select(subsegment, blogParams.blog)
        if (selectedBlogEntry == null) {
            redirectAttributes.addFlashAttribute("warning", "blogNotFound")
            return "redirect:/$HOME_DIR"
        }

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")

        logger.info("getting GUI with blogEntry. ${selectedBlogEntry.title}")
        val blogEntryForm =
            BlogEntryForm(
                selectedBlogEntry.id,
                selectedBlogEntry.segment,
                selectedBlogEntry.title,
                selectedBlogEntry.summary,
            )
        model.addAttribute("changed", selectedBlogEntry.changed)
        model.addAttribute("blogEntryForm", blogEntryForm)
        return "blogEntries"
    }

    @PostMapping(value = ["/{segment}/{subsegment}", "/{segment}"])
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
        response: HttpServletResponse,
        model: Model,
    ): String {
        val path = request.servletPath
        redirectAttributes.addFlashAttribute("action", action)
        logger.info("blog entry: path: $path action:  $action blogid: $blogId")
        if (action == "save" || action == "saveCreate") {
            checkSegment(blogEntryForm.segment, "blogEntryForm", "segment", bindingResult)
            checkStringSize(blogEntryForm.title, MAX_TITLE_SIZE, "blogEntryForm", "title", bindingResult, 1)
            checkStringSize(blogEntryForm.summary, MAX_SUMMARY_SIZE, "blogEntryForm", "summary", bindingResult)
            if (bindingResult.hasErrors()) {
                prepare(model, request, response, segment, changed)
                return "blogEntries"
            }

            try {
                blogEntryService.save(blogId, blogEntryForm)
            } catch (e: Exception) {
                when (e) {
                    is DataAccessException, is ParentBlogException -> handleRecoverableError(e, "dbSave", bindingResult)
                    is DuplicateSegmentException ->
                        bindingResult.addFieldError("blogEntryForm", "segment", "duplicate", blogEntryForm.segment)
                    else -> throw e
                }
                prepare(model, request, response, segment, changed)
                return "blogEntries"
            }

            val newPath = "$ADMIN_DIR/$segment${if (action == "save") "/${blogEntryForm.segment}" else ""}"
            return "redirect:$newPath"
        }
        if (action == "create") {
            return "redirect:$ADMIN_DIR/$segment"
        } else {
            try {
                blogEntryService.delete(blogId, blogEntryForm)
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
        logger.info("Prepare allBlogEntries Fetch blog entries with: $blogParams")

        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
        model.addAttribute("changed", changed)
        logger.info("prepared)")
    }

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
}
