package com.techreier.edrops.controllers

import com.techreier.edrops.domain.TOPIC_DEFAULT
import com.techreier.edrops.config.logger
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
        val doc = Doc(HOME, Topic(TOPIC_DEFAULT, LanguageCode("", blogParams.usedLangCode)))
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
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        redirectAttributes: RedirectAttributes,
    ): String {
        val blogParams = fetchBlogParams(model, request, response)
        val docIndex = Docs.getDocIndex(home, blogParams.oldLangCode, blogParams.usedLangCode, segment)
        if (docIndex.error) {
            if (docIndex.index < 0) {
                redirectAttributes.addFlashAttribute("warning", "blogNotFound")
                return "redirect:/$HOME_DIR"
            } else {
                model.addAttribute("docLangCode", blogParams.oldLangCode)
            }
        }
        val doc = home[docIndex.index]
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
