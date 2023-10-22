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

const val ABOUT="about"
const val ABOUT_DIR= "/$ABOUT"

@Controller
@RequestMapping(ABOUT_DIR)
class AboutController(dbService: DbService) : BaseController(dbService) {
    @GetMapping("/{tag}")
    fun content(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String?,
                request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, langCode)
        val docIndex = getDocIndex(blogParams.langCode, tag)
        if (docIndex < 0) { //tag is not found, redirect to default page with same language
            val redirectIndex = getDocIndex(blogParams.langCode)
            val doc = doc[redirectIndex]
            return "redirect:$ABOUT_DIR/${doc.tag}"
        }
        val doc = doc[docIndex]

        val docText: String = markdownToHtml(doc)
        model.addAttribute("doc", doc)
        model.addAttribute("docText", docText)
        model.addAttribute("about",true)
        return ABOUT
    }

    // Redirect to page with tag in path
    @GetMapping
    fun redirect(@RequestParam(required = false, name = "lang") language: String?,
                 request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request, language)
        val docIndex = getDocIndex(blogParams.langCode)
        val doc = doc[docIndex]
        return "redirect:$ABOUT_DIR/${doc.tag}"
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes, doc: String): String {
        logger.debug("about redirect")
        return "redirect:$ABOUT_DIR/$doc"
    }
}