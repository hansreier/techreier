package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Doc
import com.techreier.edrops.util.HOME
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

const val WELCOME = "welcome"

@Controller
@RequestMapping()
class WelcomeController(dbService: DbService) : BaseController(dbService) {

    //Get language set from session or parameter?
    @GetMapping
    fun welcome(
        @PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest, model: Model
    ): String {
        logger.info("GET $WELCOME")
        val blogParams = setCommonModelParameters(model, request, langCode)
        val doc = Doc(HOME, LanguageCode("", blogParams.locale.language))
        val docText: String = markdownToHtml(doc)
        logger.debug("BlogId: ${blogParams.blogId}")
        model.addAttribute("docText", docText)
        model.addAttribute("doc", doc)
        return WELCOME
    }
}