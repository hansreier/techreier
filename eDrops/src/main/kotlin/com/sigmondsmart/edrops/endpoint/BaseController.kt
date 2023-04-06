package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Blog
import com.sigmondsmart.edrops.domain.LanguageCode
import com.sigmondsmart.edrops.service.DbService
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.context.ServletContextAware
import org.springframework.web.servlet.mvc.support.RedirectAttributes

abstract class BaseController(private val dbService: DbService) : ServletContextAware {

    private var servletContext: ServletContext? = null
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    protected fun setCommonModelParameters(model: Model, request: HttpServletRequest) {
        logger.debug("set common model parameters")
        model.addAttribute("languages", fetchLanguages())
        val defaultLangcode = LocaleContextHolder.getLocale().language
        val selectedLangcode = model.getAttribute("langcode") as String?
        val langcode = selectedLangcode ?: defaultLangcode
        logger.debug("Language selected: $selectedLangcode default: $defaultLangcode used: $langcode")
        model.addAttribute("langcode", langcode)
        // Get Url path based on servletPath and send to template (avoid double slash in template)
        model.addAttribute("path", request.servletPath.removeSuffix("/"))
        model.addAttribute("blogs", fetchBlogs(langcode))
    }

    @PostMapping("/language")
    fun getLanguage(request: HttpServletRequest, redirectAttributes: RedirectAttributes, code: String?,
                    blogid: Long?): String {
        logger.debug("Language selected: $code path: ${request.servletPath} blogid: $blogid")
        redirectAttributes.addFlashAttribute("langcode", code)
        //Or else id blogid used to populate menu will not be preserved
        //Alternative to use hidden field is either cookie or store in session
        //If more state is needed to use Spring session (and store session in db) is recommended.
        redirectAttributes.addFlashAttribute("blogid", blogid)
        logger.debug("before redirect to get")
        return "redirect:${controllerPath(request.servletPath)}?lang=$code"
    }

    protected fun fetchLanguages(): MutableList<LanguageCode> {
        return dbService.readLanguages()
    }

    protected fun fetchBlogs(langcode: String): MutableSet<Blog>? {
        logger.info("Fetch blogs by Owner")
        val blogs = dbService.readBlogs(1, langcode )
        logger.info("Blogs fetched")
        return blogs
    }

    private fun controllerPath(currentPath: String): String {
        return currentPath.replaceAfterLast("/", "").removeSuffix("/")
    }
}