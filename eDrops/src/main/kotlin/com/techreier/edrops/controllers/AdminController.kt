package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val ADMIN = "admin"
const val ADMIN_DIR = "/$ADMIN"

@Controller
@RequestMapping(ADMIN_DIR)
class AdminController(
    private val dbService: DbService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : BaseController(dbService, messageSource, sessionLocaleResolver, appConfig) {
    @GetMapping("/{segment}")
    fun allBlogEntries(
        @PathVariable segment: String?,
        @RequestParam(required = false, name = "lang") langCode: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = setCommonModelParameters(model, request, response, langCode, segment)
        if (blogParams.blogId == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, ADMIN)
        }
        logger.info("allBlogEntries Fetch blog entries with: $blogParams")
        val blog = dbService.readBlog(blogParams.blogId)
      //  val blog = dbService.readBlogWithSameLanguage(blogParams.blogId, blogParams.locale.language)
        if ((blogParams.action == "create" || blogParams.action == "saveCreate") && blog != null) {
            logger.info("getting GUI with new blogEntry")
            val blogEntryForm = BlogEntryForm(null, "", "", "")
            model.addAttribute("changed", null)
            model.addAttribute("blogEntryForm", blogEntryForm)
        }
        model.addAttribute("blog", blog)
        model.addAttribute("linkPath", "$ADMIN_DIR/$segment/")
        logger.info("getting GUI with blogEntries")
        return "blogEntries"
    }

    @GetMapping
    fun redirect(
        @RequestParam(required = false, name = "lang") language: String?,
        request: HttpServletRequest, response: HttpServletResponse,
        model: Model,
    ): String {
        val blogParams = setCommonModelParameters(model, request, response, language)
        return "redirect:$ADMIN_DIR/${fetchFirstBlog(blogParams.locale.language).segment}"
    }

    @PostMapping
    fun getBlogAdmin(
        redirectAttributes: RedirectAttributes,
        result: String,
    ): String {
        logger.info("Admin controller redirect: $result")
        return redirect(redirectAttributes, result, ADMIN_DIR)
    }
}
