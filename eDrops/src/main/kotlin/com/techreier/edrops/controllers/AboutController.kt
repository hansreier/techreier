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
    fun content(@PathVariable tag: String?, request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        val docIndex = getDocIndex(blogParams.langCode, tag)
        logger.debug("Tag: ${tag} DocIndex: $docIndex Language: ${blogParams.langCode} ")
        val doc = doc[docIndex]
        val docText: String = markdownToHtml(doc)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        return "about"
    }

    @GetMapping("/about")
    fun content2(request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        val doc = doc[getDocIndex(blogParams.langCode)]
        return "redirect:/about/${doc.tag}"
    }

    @PostMapping("/about")
    fun getEntry(redirectAttributes: RedirectAttributes, doc: String): String {
        return "redirect:/about/$doc"
    }
}