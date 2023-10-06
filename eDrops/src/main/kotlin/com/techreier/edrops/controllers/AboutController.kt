package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.getDocId
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/about/{tag}")
class AboutController(dbService: DbService) : BaseController(dbService) {
    @GetMapping
    fun content(@PathVariable(required = false) tag: String, request: HttpServletRequest, model: Model): String {
        logger.info("content: ${request.servletPath}")
        val blogParams = setCommonModelParameters(model, request)
        val blogIdFromPath: Long = getDocId(tag, blogParams.langCode).toLong()
        logger.info("BlogId: ${blogParams.blogId} PathBlogId: $blogIdFromPath language: ${blogParams.langCode} ")
        val doc = Docs.getDoc(if (blogIdFromPath == 0L) blogParams.blogId else blogIdFromPath)
        val docText: String = markdownToHtml(doc)
        model.addAttribute("docText", docText)
        model.addAttribute("doc", doc)
        return "about"
    }

    @PostMapping
    fun getEntry(redirectAttributes: RedirectAttributes, doc: Long): String {
        logger.info("docid: $doc")
        redirectAttributes.addFlashAttribute("blogid", doc)
        return "redirect:/about"
    }
}