package com.techreier.edrops.controllers

import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.getDocIndex
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ABOUT="about"
const val ABOUT_DIR= "/$ABOUT"

@Controller
@RequestMapping(ABOUT_DIR)
class AboutController(dbService: DbService, messageSource: MessageSource) : BaseController(dbService, messageSource) {
    @GetMapping("/{segment}")
    fun content(@PathVariable segment: String?, @RequestParam(required = false, name = "lang") langCode: String?,
                request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(ABOUT, model, request, langCode)
        val docIndex = getDocIndex(about, blogParams.locale.language, segment)
        val doc = about[docIndex]

        val docText: String = markdownToHtml(doc, ABOUT_DIR)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        return ABOUT
    }

    // Redirect to page with segment in path
    @GetMapping
    fun redirect(@RequestParam(required = false, name = "lang") language: String?,
                 request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(ABOUT, model, request, language)
        val docIndex = getDocIndex(about,blogParams.locale.language)
        val doc = about[docIndex]
        return "redirect:$ABOUT_DIR/${doc.segment}"
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes, doc: String): String {
        return "redirect:$ABOUT_DIR/$doc"
    }
}