package com.techreier.edrops.controllers

import com.techreier.edrops.config.*
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.dto.MenuItemDTO
import com.techreier.edrops.service.DbService
import com.techreier.edrops.util.Docs
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.home
import com.techreier.edrops.util.validProjectLanguageCode
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.context.ServletContextAware
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

abstract class BaseController(
    private val dbService: DbService,
    private val messageSource: MessageSource,
    private val sessionLocaleResolver: SessionLocaleResolver,
    private val appConfig: AppConfig,
) : ServletContextAware {
    private var servletContext: ServletContext? = null

    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    // We have to fetch both file based (.md) markdown and db based content to populate the dropdown menu
    // Selecting no DB removes menu items and contents stored in DB
    // Should only be used if no DB is available
    protected fun setCommonModelParameters(
        model: Model,
        request: HttpServletRequest,
        response: HttpServletResponse,
        langCode: String?,
        segment: String? = null
    ): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("auth", appConfig.auth)

        // Language code as detected for web site user or set with ?lang= parameter, not default setting on PC/browser
        model.addAttribute("languages", fetchLanguages())
        val defaultLangCode = LocaleContextHolder.getLocale().language
        val usedLangcode =
            validProjectLanguageCode(langCode ?: defaultLangCode) //Check that language code is of supported types.
        val locale = Locale.of(usedLangcode)
        sessionLocaleResolver.setLocale(request, response, locale)

        var blogId = model.getAttribute("blogId") as Long?

        // Only for controllers where it is relevant to call DB, else segment is omitted
        val blog = segment?.let {
            if (blogId == null) dbService.findBlog(usedLangcode, segment) else null
        }

        blogId = blogId ?: blog?.id

        val topicKey = (model.getAttribute("topicKey") as String?) ?: blog?.topicKey
        val action = (model.getAttribute("action") ?: "") as String
        model.addAttribute("homeDocs", Docs.getDocs(home, usedLangcode))
        model.addAttribute("aboutDocs", Docs.getDocs(about, usedLangcode))
        logger.info("BlogId: $blogId Language set: $langCode, default: $defaultLangCode used: $usedLangcode set: ${locale.language}")
        model.addAttribute("langCode", usedLangcode)
        model.addAttribute("topicKey", topicKey)
        model.addAttribute("topics", fetchTopics(usedLangcode))
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("path", path)
        model.addAttribute("menu", fetchMenu(usedLangcode))
        model.addAttribute("blogId", blogId)
        model.addAttribute("maxSummarySize", MAX_SUMMARY_SIZE)
        model.addAttribute("maxTitleSize", MAX_TITLE_SIZE)
        model.addAttribute("maxSegmentSize", MAX_SEGMENT_SIZE)
        return BlogParams(blogId, locale, action)
    }

    protected fun redirect(
        redirectAttributes: RedirectAttributes,
        result: String,
        subpath: String,
    ): String {
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
        return dbService.readBlogWithSameLanguage(blog.id, validProjectLanguageCode(langCode)) ?: blog
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
        addError(FieldError(form, field, defaultFieldValue, true, null, null, msg("error.$key")))
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
    protected fun handleRecoverableError(
        e: Exception,
        key: String,
        bindingResult: BindingResult,
    ) {
        val errorMessage = msg("error.$key") + if (e.message.isNullOrBlank()) "" else ": ${e.message}"
        logger.warn("${e.javaClass}: $errorMessage")
        bindingResult.reject("key", errorMessage)
    }

    private fun fetchLanguages(): MutableList<LanguageCode> {
        logger.debug("fetch languages from db")
        return dbService.readLanguages()
    }

    private fun fetchTopics(languageCode: String): MutableList<Topic> {
        logger.debug("fetch topics from db")
        val topics = dbService.readTopics(languageCode)
        topics.forEach { topic ->
            if (topic.text.isNullOrBlank()) {
                topic.text = msg("topic." + topic.topicKey)
            }
        }
        return topics
    }

    // Assumption: Only one owner and admin user: Me.
    // TODO: If several owners is permitted an extra level in URL must be added
    private fun fetchMenu(langCode: String): List<MenuItemDTO> {
        logger.debug("Fetch menu items by langCode: $langCode")
        val blogs = dbService.readMenu(langCode)
        logger.debug("Menu fetched")
        return blogs
    }

    //  data class BlogParams(val blogId: Long, val langCode: String)
    data class BlogParams(
        val blogId: Long?,
        val locale: Locale,
        val action: String
    )
}
