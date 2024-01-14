package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Doc
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.home
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val HOME="home"
const val HOME_DIR= ""

@Controller
@RequestMapping()
class HomeController(dbService: DbService) : BaseController(dbService) {

    @GetMapping
    fun home(
        @PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest, model: Model
    ): String {
        val blogParams = setCommonModelParameters(HOME, model, request, langCode)
        val doc = Doc(HOME, LanguageCode("", blogParams.locale.language))
        val docText: String = markdownToHtml(doc)
        logger.debug("BlogId: ${blogParams.blogId}")
        model.addAttribute("docText", docText)
        model.addAttribute("doc", doc)
        logger.info("Reier before returning home")
        return HOME
    }

    // Rules for what is allowed for web crawlers
    // Direct handling of robots.txt, instead of using the robots.txt file placed in static folder
    // It creates unwanted error if handled by the tag endpoint.
    @GetMapping("/robots.txt")
    @ResponseBody
    fun handleDefault(): String {
        val rules = """
            User-agent: *
            Disallow: /admin/
        """.trimIndent()
        logger.debug("robots.txt handling rule:\n$rules")
        return rules
    }

    @GetMapping("/{tag}")
    fun content(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String?,
                request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(HOME, model, request, langCode)
        val docIndex = Docs.getDocIndex(home, blogParams.locale.language, tag)
        if (docIndex < 0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, HOME)
        }
        val doc = home[docIndex]

        val docText: String = markdownToHtml(doc, HOME_DIR)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        return HOME
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes, doc: String): String {
        return "redirect:$HOME_DIR/$doc"
    }
}