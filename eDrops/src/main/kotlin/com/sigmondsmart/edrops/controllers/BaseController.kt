package com.sigmondsmart.edrops.controllers

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.*
import com.sigmondsmart.edrops.service.DbService
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
        val blogId = (model.getAttribute("blogid") ?: 1L) as Long
        val docname = (model.getAttribute("docname") ?: "readme.md") as String
        model.addAttribute("languages", fetchLanguages())
        val defaultLangcode = LocaleContextHolder.getLocale().language
        val selectedLangcode = model.getAttribute("langcode") as String?
        val langcode = selectedLangcode ?: defaultLangcode
        model.addAttribute("docs", fetchDocs(langcode))
        logger.debug("Language selected: $selectedLangcode default: $defaultLangcode used: $langcode")
        model.addAttribute("langcode", langcode)
        // Get Url path based on servletPath and send to template (avoid double slash in template)
        model.addAttribute("path", request.servletPath.removeSuffix("/"))
        if (db) model.addAttribute("blogs", fetchBlogs(langcode))
        model.addAttribute("blogid", blogId)
        return BlogParams(blogId, langcode, docname)
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

    private fun fetchDocs(langCode: String): List<Doc> {
        logger.info("language Code $langCode")
        return if (langCode == "nb")
            listOf(
                Doc("goals", "goalsN.md", Norwegian),
                Doc("readme", "readme.md", English)
        )
        else
            listOf(
                Doc("goals", "goals.md", English),
                Doc("readme", "readme.md", English)
            )
    }

    data class BlogParams(val blogId: Long, val langCode: String, val docname: String)
}