package com.techreier.edrops.controllers

import com.techreier.edrops.config.InitException
import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Languages
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.home
import com.techreier.edrops.util.Docs.usedLanguageCode
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.context.ServletContextAware
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.util.*

abstract class BaseController(private val dbService: DbService,
                              private val messageSource: MessageSource
) : ServletContextAware {

    private var servletContext: ServletContext? = null
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    // We have to fetch both file based (.md) markdown and db based content to populate the dropdown menu
    // Selecting no DB removes menu items and contents stored in DB
    // Should only be used if no DB is available
    // TODO The option of no DB is not really used, and it is not tested either
    protected fun setCommonModelParameters(
        menu: String,
        model: Model, request: HttpServletRequest,
        langCode: String?, segment: String? = null, db: Boolean = true
    ): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("languages", fetchLanguages(db))
        val defaultLangCode = LocaleContextHolder.getLocale().language
        val selectedLangCode = model.getAttribute("langCode") as String?
        val usedLangcode = usedLanguageCode(selectedLangCode ?: langCode ?: defaultLangCode)
        val locale = Locale.of(usedLangcode)
        val blogId = (model.getAttribute("blogId") ?: fetchBlogId(usedLangcode, segment)) as Long
        model.addAttribute("homeDocs", Docs.getDocs(home, usedLangcode))
        model.addAttribute("aboutDocs", Docs.getDocs(about, usedLangcode))
        logger.info("Menu: $menu BlogId: $blogId Language path: $langCode, selected: $selectedLangCode default: $defaultLangCode used: $usedLangcode")
        model.addAttribute("langCode", usedLangcode)
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("path", path)
        model.addAttribute("menu", menu)
        if (db) {
            model.addAttribute("blogs", fetchBlogs(usedLangcode))
        }

        model.addAttribute("blogId", blogId)
        //   model.addAttribute("blogForm",BlogForm()) not required any more
        return BlogParams(blogId, locale)
    }

    protected fun redirect(redirectAttributes: RedirectAttributes, result: String, subpath: String): String {
        // Alternatives: Hidden input fields (process entire list and select by name) or server state, this is easier
        val segment = result.substringBefore(" ", "")
        val blogId = result.substringAfter(" ", "0").toLongOrNull()
        logger.debug("Redirect params: $result segment: $segment id: $blogId redirect:$subpath/$segment")
        redirectAttributes.addFlashAttribute("blogId", blogId)
        return "redirect:$subpath/$segment"
    }

    protected fun fetchFirstBlog(langCode: String): Blog {
        logger.debug("Fetch first blogId by langCode: $langCode")
        val blog = dbService.readBlog(1L)
        if (blog?.id == null) {
            throw InitException("Cannot find default blog")
        }
        return dbService.readBlogWithSameLanguage(blog.id, usedLanguageCode(langCode)) ?: blog
    }

    // Extension function to simplify implementation of adding field error
    // Normally a default value should be added, but not required
    // (The simplified FieldError constructor with 3 arguments did not allow for default value)
    fun BindingResult.addFieldError(form: String, field: String, key: String, defaultFieldValue: String? = null) {
        addError(FieldError(form, field, defaultFieldValue,  true, null, null, msg(key)))
    }

    // Return language dependende message from any key
    protected fun msg(key: String): String {
        val locale = LocaleContextHolder.getLocale()
        return messageSource.getMessage(key, null, locale)
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

    private fun fetchBlogs(langCode: String): MutableSet<Blog> {
        logger.debug("Fetch blogs by Owner langCode: $langCode")
        val blogs = dbService.readBlogs(langCode)
        logger.debug("Blogs fetched")
        return blogs
    }

    private fun fetchBlogId(langCode: String, segment: String?): Long {
        logger.debug("Fetch blogId by segment: $segment and langCode: $langCode")
        val blogId = segment?.let { dbService.readBlog(langCode, segment)?.id } ?: -1L
        return blogId
    }

    //  data class BlogParams(val blogId: Long, val langCode: String)
    data class BlogParams(val blogId: Long, val locale: Locale)
}