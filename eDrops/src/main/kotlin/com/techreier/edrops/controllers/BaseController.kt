package com.techreier.edrops.controllers

import com.techreier.edrops.config.DEFAULT_TIMEZONE
import com.techreier.edrops.config.MAX_SEGMENT_SIZE
import com.techreier.edrops.config.MAX_SUMMARY_SIZE
import com.techreier.edrops.config.MAX_TITLE_SIZE
import com.techreier.edrops.config.logger
import com.techreier.edrops.data.MENU_SPLIT_SIZE
import com.techreier.edrops.data.SUBMENU_MIN_ITEMS
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.*
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.data.Docs.about
import com.techreier.edrops.data.Docs.views
import com.techreier.edrops.util.buildVersion
import com.techreier.edrops.util.getMenuItems
import com.techreier.edrops.util.msg
import com.techreier.edrops.util.getValidProjectLanguageCode
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
import java.time.format.DateTimeFormatter
import java.util.*

const val NEW_SEGMENT = "_new"

abstract class BaseController(
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
        admin: Boolean = false
    ): BlogParams {
        logger.debug("set common model parameters")
        model.addAttribute("auth", ctx.appConfig.auth)
        model.addAttribute("languages", fetchLanguages())
        val currentLangCode = LocaleContextHolder.getLocale().language
        val usedLangcode = getValidProjectLanguageCode(currentLangCode)
        val locale = Locale.of(usedLangcode)
        ctx.sessionLocaleResolver.setLocale(request, response, locale)
        val oldLangCode = ctx.httpSession.getAttribute("langcode") as String?

        // If segment is blank or new, do not read database
        val blog = if ((segment == NEW_SEGMENT) && admin) {
            model.addAttribute("blogHeadline", msg(ctx.messageSource, "newBlog"))
            BlogDTO(usedLangcode)
        }
        else {
            model.addAttribute("blogHeadline", )
            val foundBlog = segment?.let {
                ctx.blogService.readBlog(segment, oldLangCode, usedLangcode, timeZone(), posts, !admin)
            }
            model.addAttribute("blogHeadline", foundBlog?.subject)
            foundBlog
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

        val buildStamp =  buildVersion(ctx.appConfig.buildTime, false)
        val buildInfo = ctx.appConfig.buildTime ?. let { buildVersion(ctx.appConfig.buildTime, false)} ?: buildStamp
        model.addAttribute("buildInfo", buildInfo)
        model.addAttribute("buildStamp", buildStamp)
        ctx.appConfig.buildTime ?: model.addAttribute("buildMark", buildStamp)
        return BlogParams(blog, oldLangCode, usedLangcode, action, topicKey, topics)
    }

    // Return a formatted string of datetime given a format selected in language file by locale
    protected fun datetime(dateTime: ZonedDateTime): String {
        val formatter = DateTimeFormatter.ofPattern(msg(ctx.messageSource,"format.datetime"))
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
        logger.warn("${e.javaClass} key: error.$key ${e.message}")
        bindingResult.reject("error.$key", arrayOf(e.message), "??error.$key?? ${e.message}")
    }

    protected fun readFirstSegment(languageCode: String): String? {
        val menuItems = ctx.blogService.readMenu(languageCode)
        return if (menuItems.isNotEmpty()) {
            menuItems.first().segment
        } else null
    }

    protected fun now(): Instant = ZonedDateTime.now(ctx.httpSession.getAttribute("timezone") as?  ZoneId
        ?: ZoneId.of(DEFAULT_TIMEZONE)).toInstant()

    protected fun timeZone(): ZoneId = ctx.httpSession.getAttribute("timezone") as? ZoneId ?: ZoneId.of(DEFAULT_TIMEZONE)

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
        val usedCode = getValidProjectLanguageCode(languageCode)
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
