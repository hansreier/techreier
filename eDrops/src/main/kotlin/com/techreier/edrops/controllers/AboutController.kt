package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs.doc
import com.techreier.edrops.util.Docs.getDocIndex
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping
class AboutController(dbService: DbService) : BaseController(dbService) {
    @GetMapping("/about/{tag}")
    fun content(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String?,
                request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode)
        val docIndex = getDocIndex(blogParams.langCode, tag)
        if (docIndex == -1) throw IllegalArgumentException("Cannot find document with language code: $langCode")
        logger.debug("Tag: ${tag} DocIndex: $docIndex Language: ${blogParams.langCode} ")
        val doc = doc[docIndex]

        val docText: String = markdownToHtml(doc)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        return "about"
    }

    // Redirect to page with tag in path
    @GetMapping("/about")
    fun content2(@RequestParam(required = false, name = "lang") language: String?,
                 request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, language)
        val docIndex = getDocIndex(blogParams.langCode)
        if (docIndex == -1) throw IllegalArgumentException("Cannot find document with language code: $language")
        val doc = doc[docIndex]
        return "redirect:/about/${doc.tag}"
    }

    @PostMapping("/about")
    fun getEntry(redirectAttributes: RedirectAttributes, doc: String): String {
        logger.debug("about redirect")
        return "redirect:/about/$doc"
    }
}