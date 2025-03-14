package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.getDocIndex
import com.techreier.edrops.util.Docs.views
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val HOME = "home"
const val HOME_DIR = ""

@Controller
@RequestMapping()
class Home(context: Context ) : Base(context) {
    @GetMapping
    fun home(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = getDocIndex(Docs.home, blogParams.oldLangCode, blogParams.usedLangCode)

        if (docIndex.index >= 0 ) {
            val doc = Docs.home[docIndex.index]
            val docText: String = markdownToHtml(doc)
            model.addAttribute("doc", doc)
            model.addAttribute("docText", docText)
        } else {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return HOME
    }

    // Rules for what is allowed for web crawlers
    // Direct handling of robots.txt, instead of using the robots.txt file placed in static folder
    // It creates unwanted error if handled by the segment endpoint.
    @GetMapping("/robots.txt")
    @ResponseBody
    fun handleDefault(): String {
        val rules =
            """
            User-agent: *
            Disallow: /admin/
            """.trimIndent()
        logger.debug("robots.txt handling rule:\n$rules")
        return rules
    }

    @GetMapping("/{segment}")
    fun content(
        @PathVariable segment: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = getDocIndex(views, blogParams.oldLangCode, blogParams.usedLangCode, segment)
        if (docIndex.error) {
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            } else {
                model.addAttribute("docLangCode", blogParams.oldLangCode)
            }
        }
        val doc = views[docIndex.index]
        val docText: String = markdownToHtml(doc, HOME_DIR)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        return HOME
    }

    @PostMapping
    fun getEntry(doc: String): String {
        logger.info("Redirect to home")
        return "redirect:$HOME_DIR/$doc"
    }
}
