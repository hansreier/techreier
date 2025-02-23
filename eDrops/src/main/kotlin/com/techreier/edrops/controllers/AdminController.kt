package com.techreier.edrops.controllers


import com.techreier.edrops.config.logger
import com.techreier.edrops.forms.BlogEntryForm
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class Admin(context: Context) : Base(context) {

    @GetMapping("/{segment}")
    fun allBlogEntries(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, langCode, segment, true)
        if (blogParams.blog == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN)
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        if (blogParams.action == "create" || blogParams.action == "saveCreate") {
            logger.info("getting GUI with new blogEntry")
            val blogEntryForm = BlogEntryForm(null, "", "", "")
            model.addAttribute("changed", null)
            model.addAttribute("blogEntryForm", blogEntryForm)
        }
        model.addAttribute("blog", blogParams.blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, language)
        return "redirect:$ADMIN_DIR/${readFirstSegment(blogParams.locale.language)}"
    }

    @PostMapping
    fun getBlogAdmin(
        redirectAttributes: RedirectAttributes,
        result: String
    ): String {
        logger.info("Admin controller redirect: $result")
        return redirect(redirectAttributes, result, ADMIN_DIR)
    }
}
