package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException

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
        val blogEntryForm = BlogEntryForm(selectedBlogEntry.id, selectedBlogEntry.segment, selectedBlogEntry.title,
            selectedBlogEntry.summary, selectedBlogEntry.changed)
        model.addAttribute("blogEntryForm", blogEntryForm)
        return "blogEntries"
    }

}