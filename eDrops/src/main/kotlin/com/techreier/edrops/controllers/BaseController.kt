package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.usedLanguageCode
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.context.ServletContextAware
import org.springframework.web.servlet.mvc.support.RedirectAttributes

abstract class BaseController(private val dbService: DbService) : ServletContextAware {

    private var servletContext: ServletContext? = null
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    // Selecting no DB removes menu items and contents stored in DB
    // Should only be used if no DB is available
    // TODO The option of no DB is not really used, and it is not tested either
    protected fun setCommonModelParameters(model: Model, request: HttpServletRequest,
                                           pathLangcode: String?, tag: String? = null, db: Boolean = true): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("languages", fetchLanguages(db))
        val defaultLangcode = LocaleContextHolder.getLocale().language
        val selectedLangcode = model.getAttribute("langcode") as String?
        val langcode = usedLanguageCode(selectedLangcode ?: pathLangcode ?: defaultLangcode)
        val blogId = (model.getAttribute("blogid") ?: fetchBlogId(langcode, tag)) as Long
        model.addAttribute("docs", Docs.getDocs(langcode)) //TODO only needed for about menu
        logger.debug("Language path: $pathLangcode, selected: $selectedLangcode default: $defaultLangcode used: $langcode")
        model.addAttribute("langcode", langcode)
        // Get Url path based on servletPath and send to template (avoid double slash in template)
        model.addAttribute("path", request.servletPath.removeSuffix("/"))
        if (db) {
            model.addAttribute("blogs", fetchBlogs(langcode))
        }

        model.addAttribute("blogid", blogId)
     //   model.addAttribute("blogForm",BlogForm()) not required any more
        return BlogParams(blogId, langcode)
    }

    protected fun redirect(redirectAttributes: RedirectAttributes, result: String, subpath: String): String {
        // Alternatives: Hidden input fields (process entire list and select by name) or server state, this is easier
        val tag = result.substringBefore(" ","")
        val blogId = result.substringAfter(" ","0").toLongOrNull()
        logger.info("blog tag: $tag id: $blogId")
        redirectAttributes.addFlashAttribute("blogid", blogId)
        return "redirect:$subpath/$tag"
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
        logger.info("Fetch blogs by Owner langcode: $langcode")
        val blogs = dbService.readBlogs(1, langcode)
        logger.info("Blogs fetched")
        return blogs
    }

    private fun fetchBlogId(langcode: String, tag:  String?): Long {
        logger.info("Fetch blogId by tag and langcode: $langcode")
        val blogId = tag ?.let {dbService.readBlog(langcode, tag)?.id  } ?: 1L
        return blogId
    }
    data class BlogParams(val blogId: Long, val langCode: String)
}