package com.techreier.edrops.controllers

import com.techreier.edrops.config.InitException
import com.techreier.edrops.config.logger
import com.techreier.edrops.util.Doc
import com.techreier.edrops.util.HOME
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.markdownToHtml
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping()
class WelcomeController(dbService: DbService) : BaseController(dbService) {

    //Get language set from session or parameter?
    @GetMapping
    fun welcome(@PathVariable tag: String?, @RequestParam(required = false, name = "lang") langCode: String?,
                request: HttpServletRequest, model: Model): String {
        try {
            logger.debug("welcome")
            val blogParams = setCommonModelParameters(model, request, langCode)
            val doc =  Doc(HOME, LanguageCode("",blogParams.langCode))
            val docText: String = markdownToHtml(doc)
            logger.debug("BlogId: ${blogParams.blogId}")
            logger.debug("Text: $docText")
            model.addAttribute("docText", docText)
            model.addAttribute("doc", doc)
        } catch (e: Exception) {
            throw (InitException("Cannot open default page", e)) //Rethrow so can be picked by error handler.
        }
        return "welcome"
    }

    // https://www.thymeleaf.org/doc/articles/standardurlsyntax.htmlzzzzz
    // https://stackoverflow.com/questions/26326559/thymeleaf-thhref-invoking-both-a-post-and-get
    // does not work
    // https://www.baeldung.com/thymeleaf-select-option
    @RequestMapping(value = ["/change"], method = [RequestMethod.GET])
    fun checkEventForm(model: Model): String? {
        logger.info("check event form")
        return "redirect:/"
    }

    @PostMapping("/blogs2")
    fun getBlog(redirectAttributes: RedirectAttributes, blog: String): String {
        logger.info("blogs2 valgt blog: $blog")
        redirectAttributes.addFlashAttribute("blogid", blog)
        return "redirect:/"
    }
}