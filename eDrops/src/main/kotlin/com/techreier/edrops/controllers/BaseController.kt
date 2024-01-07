package com.techreier.edrops.controllers

import com.techreier.edrops.config.InitException
import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Languages
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.usedLanguageCode
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.context.ServletContextAware
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

abstract class BaseController(private val dbService: DbService) : ServletContextAware {

    private var servletContext: ServletContext? = null
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    // We have to fetch both file based (.md) markdown and db based content to populate the dropdown menu
    // Selecting no DB removes menu items and contents stored in DB
    // Should only be used if no DB is available
    // TODO The option of no DB is not really used, and it is not tested either
    protected fun setCommonModelParameters(
        model: Model, request: HttpServletRequest,
        pathLangcode: String?, tag: String? = null, db: Boolean = true
    ): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("languages", fetchLanguages(db))
        val defaultLangcode = LocaleContextHolder.getLocale().language
        val selectedLangcode = model.getAttribute("langcode") as String?
        val langcode = usedLanguageCode(selectedLangcode ?: pathLangcode ?: defaultLangcode)
        val locale = Locale(langcode)
        val blogId = (model.getAttribute("blogid") ?: fetchBlogId(langcode, tag)) as Long
        model.addAttribute("docs", Docs.getDocs(langcode))
        logger.debug("BlogId: $blogId Language path: $pathLangcode, selected: $selectedLangcode default: $defaultLangcode used: $langcode")
        model.addAttribute("langcode", langcode)
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("path", path)
        model.addAttribute("menu", path.removePrefix("/").substringBeforeLast("/"))
        if (db) {
            model.addAttribute("blogs", fetchBlogs(langcode))
        }

        model.addAttribute("blogid", blogId)
        //   model.addAttribute("blogForm",BlogForm()) not required any more
        return BlogParams(blogId, locale)
    }

    protected fun redirect(redirectAttributes: RedirectAttributes, result: String, subpath: String): String {
        // Alternatives: Hidden input fields (process entire list and select by name) or server state, this is easier
        val tag = result.substringBefore(" ", "")
        val blogId = result.substringAfter(" ", "0").toLongOrNull()
        logger.debug("Redirect params: $result tag: $tag id: $blogId redirect:$subpath/$tag")
        redirectAttributes.addFlashAttribute("blogid", blogId)
        return "redirect:$subpath/$tag"
    }

    protected fun fetchFirstBlog(langcode: String): Blog {
        logger.debug("Fetch first blogId by langcode: $langcode")
        val blog = dbService.readBlog(1L)
        if (blog?.id == null) {
            throw InitException("Cannot find default blog")
        }
        return dbService.readBlogWithSameLanguage(blog.id, usedLanguageCode(langcode)) ?: blog
    }

    private fun fetchLanguages(db: Boolean = true): MutableList<LanguageCode> {
        return if (db) {
            logger.debug("fetch languages from db")
            dbService.readLanguages()
        } else {
            logger.debug("fetch languages from code")
            Languages.toMutableList()
        }
    }

    private fun fetchBlogs(langcode: String): MutableSet<Blog> {
        logger.debug("Fetch blogs by Owner langcode: $langcode")
        val blogs = dbService.readBlogs(1, langcode)
        logger.debug("Blogs fetched")
        return blogs
    }

    private fun fetchBlogId(langcode: String, tag: String?): Long {
        logger.debug("Fetch blogId by tag: $tag and langcode: $langcode")
        val blogId = tag?.let { dbService.readBlog(langcode, tag)?.id } ?: -1L
        return blogId
    }

    //  data class BlogParams(val blogId: Long, val langCode: String)
    data class BlogParams(val blogId: Long, val locale: Locale)
}