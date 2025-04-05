package com.techreier.edrops.controllers

import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.views
import com.techreier.edrops.util.buildVersion
import com.techreier.edrops.util.getMenuItems
import com.techreier.edrops.util.msg
import com.techreier.edrops.util.validProjectLanguageCode
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.context.ServletContextAware
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val NEW_SEGMENT = "_"

abstract class Base(
    private val ctx: Context,
) : ServletContextAware {
    private var servletContext: ServletContext? = null

    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    // We have to fetch both file based (.md) markdown and db based content to populate the dropdown menu
    // Selecting no DB removes menu items and contents stored in DB
    // Should only be used if no DB is available
    protected fun fetchBlogParams(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        segment: String? = null,
        posts: Boolean = false,
    ): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("auth", ctx.appConfig.auth)
        model.addAttribute("languages", fetchLanguages())
        val currentLangCode = LocaleContextHolder.getLocale().language
        val usedLangcode = validProjectLanguageCode(currentLangCode)
        val locale = Locale.of(usedLangcode)
        ctx.sessionLocaleResolver.setLocale(request, response, locale)
        val oldLangCode = ctx.httpSession.getAttribute("langcode") as String?

        // Only for controllers where it is relevant to call DB, else segment is omitted
        val blog = if (segment == NEW_SEGMENT)
            BlogDTO(usedLangcode)
        else
            segment?.let { ctx.blogService.readBlog(segment, oldLangCode, usedLangcode, posts) }
        ctx.httpSession.setAttribute("langcode", blog?.langCodeFound ?: usedLangcode)

        val topics = fetchTopics(usedLangcode)
        val topicKey =
            if (topics.isNotEmpty()) {
                (ctx.httpSession.getAttribute("topic") as String?) ?: topics.first().topicKey
            } else {
                TOPIC_DEFAULT
            }

        val action = (model.getAttribute("action") ?: "") as String
        model.addAttribute("homeMenu", fetchMenuFromDisk(views, usedLangcode))
        model.addAttribute("aboutMenu", fetchMenuFromDisk(about, usedLangcode))
        model.addAttribute("langCode", usedLangcode)
        model.addAttribute("topicKey", topicKey)
        model.addAttribute("topics", topics)
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("path", path)
        model.addAttribute("menu", fetchMenuFromDb(usedLangcode))
        model.addAttribute("maxSummarySize", MAX_SUMMARY_SIZE)
        model.addAttribute("maxTitleSize", MAX_TITLE_SIZE)
        model.addAttribute("maxSegmentSize", MAX_SEGMENT_SIZE)
        model.addAttribute("buildDate", buildVersion(ctx.appConfig.buildTime))
        return BlogParams(blog, oldLangCode, usedLangcode, action, topicKey, topics)
    }

    // Extension function to simplify implementation of adding field error
    // Normally a default value should be added, but not required
    // (The simplified FieldError constructor with 3 arguments did not allow for default value)
    fun BindingResult.addFieldError(
        form: String,
        field: String,
        key: String,
        defaultFieldValue: String? = null,
    ) {
        addError(FieldError(form, field, defaultFieldValue, true, null, null,
            msg(ctx.messageSource,"error.$key")))
    }

    protected fun checkSegment(
        value: String?,
        form: String,
        field: String,
        bindingResult: BindingResult,
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
        value: String?,
        maxSize: Int,
        form: String,
        field: String,
        bindingResult: BindingResult,
        minSize: Int = 0,
    ) {
        if (value.isNullOrBlank()) {
            if (minSize == 1) {
                bindingResult.addFieldError(form, field, "empty", value)
            }
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

    // Return a formatted string of datetime given a format selected in language file by locale
    // It was planned to be used, but is not. Not tested: What happens if datetime.format is undefined.
    protected fun datetime(dateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern(msg(ctx.messageSource,"datetime.format"))
        return dateTime.format(formatter)
    }

    // Logg and handle a general recoverable error to be presented in Thymeleaf
    // Note: Stacktrace not logged, should it?
    // The primary error text is fetched from language files with "error.key"
    protected fun handleRecoverableError(
        e: Exception,
        key: String,
        bindingResult: BindingResult,
    ) {
        val errorMessage = msg(ctx.messageSource, "error.$key") + if (e.message.isNullOrBlank()) "" else ": ${e.message}"
        logger.warn("${e.javaClass}: $errorMessage")
        bindingResult.reject("key", errorMessage)
    }

    protected fun readFirstSegment(languageCode: String): String? {
        val menuItems = ctx.blogService.readMenu(languageCode)
        return if (menuItems.isNotEmpty()) {
            menuItems.first().segment
        } else null
    }

    private fun fetchLanguages(): MutableList<LanguageCode> {
        logger.debug("fetch languages from db")
        return ctx.genService.readLanguages()
    }

    private fun fetchTopics(languageCode: String): MutableList<Topic> {
        logger.debug("fetch topics from db")
        val topics = ctx.genService.readTopics(languageCode)
        topics.forEach { topic ->
            if (topic.text.isNullOrBlank()) {
                topic.text = msg(ctx.messageSource,"topic." + topic.topicKey)
            }
        }
        return topics
    }

    // Fetch menu items from database (Note: Only own owner in this implementation)
    private fun fetchMenuFromDb(langCode: String): List<MenuItem> {
        logger.debug("Fetch menu items by langCode: $langCode")
        val blogs = ctx.blogService.readMenu(langCode)
        return getMenuItems(blogs, SUBMENU_MIN_ITEMS, MENU_SPLIT_SIZE, ctx.messageSource)
    }

    // Fetch menu items from documents stored on disk
    private fun fetchMenuFromDisk(
        docs: Array<MenuItem>,
        languageCode: String,
    ): List<MenuItem> {
        val usedCode = validProjectLanguageCode(languageCode)
        val documents = docs.filter { (it.langCode == usedCode) }
        return getMenuItems(documents, SUBMENU_MIN_ITEMS, MENU_SPLIT_SIZE,  ctx.messageSource)
    }


    data class BlogParams(
        val blog: BlogDTO?,
        val oldLangCode: String?,
        val usedLangCode: String,
        val action: String,
        val topicKey: String,
        val topics: List<Topic>
    )

    companion object
}
