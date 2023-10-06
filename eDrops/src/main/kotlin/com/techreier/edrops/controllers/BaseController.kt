package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.context.ServletContextAware

abstract class BaseController(private val dbService: DbService) : ServletContextAware {

    private var servletContext: ServletContext? = null
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    // Selecting no DB removes menu items and contents stored in DB
    // Should only be used if no DB is available
    protected fun setCommonModelParameters(model: Model, request: HttpServletRequest, db: Boolean = true): BlogParams {
        logger.debug("set common model parameters")
        val blogId = (model.getAttribute("blogid") ?: 0L) as Long
        model.addAttribute("languages", fetchLanguages())
        val defaultLangcode = LocaleContextHolder.getLocale().language
        val selectedLangcode = model.getAttribute("langcode") as String?
        val langcode = selectedLangcode ?: defaultLangcode
        model.addAttribute("docs", Docs.getDocs(langcode)) //TODO only needed for about menu
        logger.debug("Language selected: $selectedLangcode default: $defaultLangcode used: $langcode")
        model.addAttribute("langcode", langcode)
        // Get Url path based on servletPath and send to template (avoid double slash in template)
        model.addAttribute("path", request.servletPath.removeSuffix("/"))
        if (db) model.addAttribute("blogs", fetchBlogs(langcode))
        model.addAttribute("blogid", blogId)
        return BlogParams(blogId, langcode)
    }

    private fun fetchLanguages(db: Boolean = true): MutableList<LanguageCode> {
        return if (db) {
            dbService.readLanguages()
        } else {
            Languages.toMutableList()
        }
    }

    private fun fetchBlogs(langcode: String): MutableSet<Blog> {
        logger.info("Fetch blogs by Owner")
        val blogs = dbService.readBlogs(1, langcode)
        logger.info("Blogs fetched")
        return blogs
    }
    data class BlogParams(val blogId: Long, val langCode: String)
}