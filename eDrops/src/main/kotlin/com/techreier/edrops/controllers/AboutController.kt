package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
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
    @GetMapping(path= ["/about", "/about/{tag}"])
    fun content(@PathVariable(required = false) tag: String?, request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(model, request)
        val docIndex: Long = getDocIndex(tag, blogParams.langCode).toLong()
        logger.debug("Tag: ${tag} BlogId: ${blogParams.blogId} DocIndex: $docIndex Language: ${blogParams.langCode} ")
        val doc = Docs.getDoc(if (docIndex == 0L) blogParams.blogId else docIndex)
        val docText: String = markdownToHtml(doc)
        model.addAttribute("docText", docText)
        model.addAttribute("doc", doc)
        return "about"
    }

    @PostMapping("/about")
    fun getEntry(redirectAttributes: RedirectAttributes, doc: String): String {
        return "redirect:/about/" + doc
    }
}