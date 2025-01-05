package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN="admin"
const val ADMIN_DIR= "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class AdminController(private val dbService: DbService,
                      messageSource: MessageSource): BaseController(dbService, messageSource)
{
    @GetMapping("/{segment}")
    fun allBlogEntries(@PathVariable segment: String?, @RequestParam(required = false, name = "lang") langCode: String?,
                       request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(ADMIN, model, request, langCode, segment)
        if (blogParams.blogId <0) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN)
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.locale.language )
        model.addAttribute("blog", blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/${segment}/")
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping
    fun redirect(@RequestParam(required = false, name = "lang") language: String?,
                 request: HttpServletRequest, model: Model): String {
        val blogParams = setCommonModelParameters(ADMIN, model, request, language)
        return "redirect:$ADMIN_DIR/${fetchFirstBlog(blogParams.locale.language).segment}"
    }

    @PostMapping
    fun getBlogAdmin(redirectAttributes: RedirectAttributes, result: String): String {
        logger.info("Admin controller redirect: $result")
        return redirect(redirectAttributes, result, ADMIN_DIR)
    }

}