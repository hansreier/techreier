package com.techreier.edrops.controllers

import com.techreier.edrops.config.*
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

abstract class BaseController(
    private val dbService: DbService,
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
        model.addAttribute("auth", false)
        val defaultLangCode = LocaleContextHolder.getLocale().language
        val usedLangcode = usedLanguageCode(langCode ?: defaultLangCode)
        val locale = Locale.of(usedLangcode)
        val blogId = (model.getAttribute("blogId") ?: fetchBlogId(usedLangcode, segment)) as Long
        val action = (model.getAttribute("action") ?: "") as String
        model.addAttribute("homeDocs", Docs.getDocs(home, usedLangcode))
        model.addAttribute("aboutDocs", Docs.getDocs(about, usedLangcode))
        logger.info("Menu: $menu BlogId: $blogId Language set: $langCode, default: $defaultLangCode used: $usedLangcode")
        model.addAttribute("langCode", usedLangcode)
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("path", path)
        model.addAttribute("menu", menu)
        if (db) {
            model.addAttribute("blogs", fetchBlogs(usedLangcode))
        }

        model.addAttribute("blogId", blogId)
        model.addAttribute("maxSummarySize", MAX_SUMMARY_SIZE)
        model.addAttribute("maxTitleSize", MAX_TITLE_SIZE)
        model.addAttribute("maxSegmentSize", MAX_SEGMENT_SIZE)
        return BlogParams(blogId, locale, action)
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
        addError(FieldError(form, field, defaultFieldValue, true, null, null, msg("error.$key")))
    }

    protected fun checkSegment(
        value: String?, form: String, field: String,
        bindingResult: BindingResult
    ) {
        val regex = "^[a-z](?:[a-z0-9-]*[a-z0-9])?$".toRegex()

        if (value.isNullOrBlank()) {
            logger.info("$form $field: used in URL and cannot be empty")
            bindingResult.addFieldError(form, field, "empty", value)
            return
        }
        if (value.length > MAX_SEGMENT_SIZE) {
            logger.info("$form $field: ${value.length} is longer than the allowed size: $MAX_SEGMENT_SIZE")
            bindingResult.addFieldError(form, field, "maxSize", value)
            return
        }
        if (!value.matches(regex)) {
            logger.info("$form $field: used in URL, use lower case and no special characters ")
            bindingResult.addFieldError(form, field, "segment", value)
        }
    }

    protected fun checkStringSize(
        value: String?, maxSize: Int, form: String, field: String,
        bindingResult: BindingResult, minSize: Int = 0
    ) {
        if (value.isNullOrBlank()) {
            if (minSize == 1)
                bindingResult.addFieldError(form, field, "empty", value)
            return
        }
        if (value.length > maxSize) {
            logger.info("$form $field: ${value.length} is longer than the allowed size: $maxSize")
            bindingResult.addFieldError(form, field, "maxSize", value)
            return
        }
        if (value.length < minSize) {
            logger.info("$form $field: ${value.length} is shorter than the minimum size: $minSize")
            bindingResult.addFieldError(form, field, "minSize", value)
            return
        }
        val byteSize = value.toByteArray(Charsets.UTF_8).size
        if (byteSize > maxSize) {
            logger.info("$form $field: $byteSize (checked for multibyte) is longer than the allowed size: $maxSize")
            bindingResult.addFieldError(form, field, "maxSizeM", value)
        }
    }

    // Return language dependent message from any key
    protected fun msg(key: String): String {
        val locale = LocaleContextHolder.getLocale()
        return messageSource.getMessage(key, null, "??$key??", locale) as String
    }

    // Return a formatted string of datetime given a format selected in language file by locale
    // It was planned to be used, but is not. Not tested: What happens if datetime.format is undefined.
    protected fun datetime(dateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern(msg("datetime.format"))
        return dateTime.format(formatter)
    }

    // Logg and handle a general recoverable error to be presented in Thymeleaf
    // Note: Stacktrace not logged, should it?
    // The primary error text is fetched from language files with "error.key"
    protected fun handleRecoverableError(e: Exception, key: String, bindingResult: BindingResult) {
        val errorMessage = msg("error.$key") + if (e.message.isNullOrBlank()) "" else ": ${e.message}"
        logger.warn("${e.javaClass}: $errorMessage")
        bindingResult.reject("key", errorMessage)
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

    // TODO change so does not read Blog summary
    // @Query("SELECT new com.yourpackage.BlogMenuDto(be.id, be.title) FROM BlogEntry be WHERE be.blog.id = :blogId")
    // Return DTO instead
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
    data class BlogParams(val blogId: Long, val locale: Locale, val action: String)
}