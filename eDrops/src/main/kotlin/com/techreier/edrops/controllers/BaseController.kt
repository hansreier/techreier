package com.techreier.edrops.controllers

import com.techreier.edrops.config.*
import com.techreier.edrops.data.Docs.about
import com.techreier.edrops.data.Docs.views
import com.techreier.edrops.data.MENU_SPLIT_SIZE
import com.techreier.edrops.data.SUBMENU_MIN_ITEMS
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.util.Markdown
import com.techreier.edrops.util.buildVersion
import com.techreier.edrops.util.getMenuItems
import com.techreier.edrops.util.getValidProjectLanguageCode
import com.techreier.edrops.util.msg
import jakarta.servlet.ServletContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.context.ServletContextAware
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

const val DUMMY_ID="/0"
const val NEW_SEGMENT="_new"
const val NEW_SUBSEGMENT = "_new$DUMMY_ID"

abstract class BaseController(
    private val ctx: Context,
) : ServletContextAware {
    private var servletContext: ServletContext? = null
    protected val markdown: Markdown = Markdown()

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
        admin: Boolean = false
    ): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("auth", ctx.appConfig.auth)
        model.addAttribute("languages", fetchLanguages())
        val currentLangCode = LocaleContextHolder.getLocale().language
        val usedLangcode = getValidProjectLanguageCode(currentLangCode)
        val locale = Locale.of(usedLangcode)
        ctx.sessionLocaleResolver.setLocale(request, response, locale) //Set locale to allowed projectLocale
        val oldLangCode = ctx.httpSession.getAttribute("langcode") as String?
        // If segment is blank or new, do not read database
        val blog = if ((segment == NEW_SEGMENT) && admin) {
            model.addAttribute("blogHeadline", msg(ctx.messageSource, "newBlog"))
            BlogDTO(usedLangcode)
        } else {
            model.addAttribute("blogHeadline", "")
            val foundBlog = segment?.let {
                ctx.blogService.readBlog(segment, oldLangCode, usedLangcode, posts, admin)
            }
            if (foundBlog == null) {
                model.addAttribute("blogHeadline", msg(ctx.messageSource, "noBlog"))
                null
            } else {
                val blogLangCode = foundBlog.topic.language.code
                val locale = Locale.of(blogLangCode)
                ctx.sessionLocaleResolver.setLocale(request, response, locale) //Required sometimes
                model.addAttribute("blogHeadline", foundBlog.subject)
                foundBlog.toDTO(
                    zoneId = timeZone(),
                    datetimePattern = msg(ctx.messageSource, "format.datetime"),
                    datePattern = msg(ctx.messageSource, "format.date"),
                    Markdown(),
                    langCodeWanted =blogLangCode, posts, !admin
                )
            }
        }

        ctx.httpSession.setAttribute("langcode", blog?.langCodeFound ?: usedLangcode)

        val topics = fetchTopics(usedLangcode)
        val topicKey =
            if (topics.isNotEmpty()) {
                (ctx.httpSession.getAttribute("topic") as String?) ?: topics.first().topicKey
            } else {
                TOPIC_DEFAULT
            }

        val action = (model.getAttribute("action") ?: "") as String
        model.addAttribute("blogHeadLine", blog?.subject ?: "")
        model.addAttribute("homeMenu", fetchMenuFromDisk(views, usedLangcode))
        model.addAttribute("aboutMenu", fetchMenuFromDisk(about, usedLangcode))
        model.addAttribute("langCode", usedLangcode)
        model.addAttribute("topicKey", topicKey)
        model.addAttribute("topics", topics)
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("newSegment", NEW_SEGMENT)
        model.addAttribute("path", path)
        model.addAttribute("menu", fetchMenuFromDb(usedLangcode))
        model.addAttribute("maxSummarySize", MAX_SUMMARY_SIZE)
        model.addAttribute("maxTitleSize", MAX_TITLE_SIZE)
        model.addAttribute("maxSegmentSize", MAX_SEGMENT_SIZE)
        // Build time if existing or current time. Forcing clear of css cache. Value in GUI at bottom.
        // Current time should only be used for selected local profiles, refer to BuildTimeValidation.kt
        val built = buildVersion(ctx.appConfig.buildTime, false)
        model.addAttribute("built", built)
        return BlogParams(blog, oldLangCode, usedLangcode, action, topicKey, topics)
    }

    //Current time in Europe / Oslo time
    protected fun now(): Instant = ZonedDateTime.now(timeZone()).toInstant()

    //Time zone in Europe / Oslo time
    protected fun timeZone(): ZoneId =
        ctx.httpSession.getAttribute("timezone") as? ZoneId
            ?: ZoneId.of(DEFAULT_TIMEZONE)

    // Logg and handle a general recoverable error to be presented in Thymeleaf
    // Note: Stacktrace not logged, should it?
    // The primary error text is fetched from language files with "error.key"
    protected fun handleRecoverableError(
        e: Exception,
        key: String,
        bindingResult: BindingResult,
    ) {
        logger.warn("${e.javaClass} key: error.$key ${e.message}")

        val args: Array<out Any> = arrayOf(e.message ?: "")
        bindingResult.reject("error.$key", args, "??error.$key?? ${e.message}")
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
                topic.text = msg(ctx.messageSource, "topic." + topic.topicKey)
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
        val usedCode = getValidProjectLanguageCode(languageCode)
        val documents = docs.filter { (it.langCode == usedCode) }
        return getMenuItems(documents, SUBMENU_MIN_ITEMS, MENU_SPLIT_SIZE, ctx.messageSource)
    }

    data class BlogParams(
        val blog: BlogDTO?,
        val oldLangCode: String?,
        val usedLangCode: String,
        val action: String,
        val topicKey: String,
        val topics: List<Topic>,
    ) {
        override fun toString() =
            "action=$action, key=$topicKey, lang=$oldLangCode=>$usedLangCode, segment=${blog?.segment}"
    }

}
