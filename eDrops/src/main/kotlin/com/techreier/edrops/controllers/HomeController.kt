package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.DEFAULT
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.util.Doc
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.home
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

const val HOME = "home"
const val HOME_DIR = ""

@Controller
@RequestMapping()
class Home(context: Context ) : Base(context) {
    @GetMapping
    fun home(
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, langCode)
        val doc = Doc(HOME, Topic(DEFAULT, LanguageCode("", blogParams.locale.language)))
        val docText: String = markdownToHtml(doc)
        model.addAttribute("docText", docText)
        model.addAttribute("doc", doc)
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
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = fetchBlogParams(model, request, response, langCode)
        val docIndex = Docs.getDocIndex(home, blogParams.locale.language, segment)
        val doc = home[docIndex]

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
