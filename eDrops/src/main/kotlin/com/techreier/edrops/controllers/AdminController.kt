package com.techreier.edrops.controllers


import com.techreier.edrops.config.logger
import com.techreier.edrops.forms.BlogEntryForm
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class Admin(context: Context) : Base(context) {

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

    @PostMapping
    fun getBlogAdmin(
        result: String
    ): String {
        return "redirect:$ADMIN_DIR/${result}"
    }
}
