package com.techreier.edrops.controllers

import com.techreier.edrops.config.*
import com.techreier.edrops.data.Docs.about
import com.techreier.edrops.data.Docs.getDocIndex
import com.techreier.edrops.data.Docs.views
import com.techreier.edrops.data.TOPIC_DEFAULT
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.BlogParams
import com.techreier.edrops.dto.BlogPrincipal
import com.techreier.edrops.dto.BlogWithPosts
import com.techreier.edrops.dto.MenuItem
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.exceptions.NotAuthorizedException
import com.techreier.edrops.util.*
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

abstract class BaseController(
    private val ctx: Context,
) : ServletContextAware {
    private var servletContext: ServletContext? = null
    protected val markdown  = ctx.markdown

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
        menuUsed: Menu,
        segment: String? = null,
        posts: Boolean = false,
        forcedLangcode: String? = null
    ): BlogParams {
        logger.debug("set common model parameters")
        val adminMenu = menuUsed.equals(Menu.ADM)
        model.addAttribute("menuUsed", menuUsed.name.lowercase())
        model.addAttribute("auth", ctx.appConfig.auth)
        model.addAttribute("languages", fetchLanguages())
        val currentLangCode = forcedLangcode ?: LocaleContextHolder.getLocale().language
        val usedLangCode = getValidProjectLanguageCode(currentLangCode)
        val locale = Locale.of(usedLangCode)
        ctx.sessionLocaleResolver.setLocale(request, response, locale) //Set locale to allowed projectLocale
        val oldLangCode = ctx.httpSession.getAttribute("langcode") as String?
        // If segment is blank or new, do not read database
        val blog = if ((segment == NEW_SEGMENT) && adminMenu) {
            model.addAttribute("blogHeadline", msg(ctx.messageSource, "newBlog"))
            BlogDTO(usedLangCode)
        } else {
            model.addAttribute("blogHeadline", "")
            val blogWithPosts: BlogWithPosts? = segment?.let {
                ctx.blogService.readBlog(segment,  oldLangCode, usedLangCode, posts, adminMenu) ?: noBlog(model)
            }
           blogWithPosts?.let { bwp ->
                val blogDto = bwp.toDTO(
                    zoneId = timeZone(),
                    datetimePattern = msg(ctx.messageSource, "format.datetime"),
                    datePattern = msg(ctx.messageSource, "format.date"),
                    markdown = ctx.markdown,
                    langCodeWanted = usedLangCode,
                    posts = posts,
                    html = !adminMenu
                )
                 if (blogDto.langCodeFound != blogDto.langCodeWanted) {
                     val otherLanguage = msg(ctx.messageSource, "warning.otherLanguage", blogDto.langCodeWanted)
                     model.addAttribute("otherLanguage",otherLanguage)
                 }
                model.addAttribute("blogHeadline", blogDto.subject)
                blogDto
            }
        }

        val blogLangcode = blog?.langCodeFound ?: usedLangCode
        ctx.httpSession.setAttribute("langcode", blogLangcode)
        model.addAttribute("blogLangcode", blogLangcode)
        val topics = fetchTopics(usedLangCode)
        val topicKey =
            if (topics.isNotEmpty()) {
                (ctx.httpSession.getAttribute("topic") as String?) ?: topics.first().topicKey
            } else {
                TOPIC_DEFAULT
            }

        val action = (model.getAttribute("action") ?: "") as String
        model.addAttribute("menuChanged", ctx.genService.menuChanged())
        model.addAttribute("blogHeadLine", blog?.subject ?: "")
        model.addAttribute("homeMenu", fetchMenuFromDisk(views, usedLangCode))
        model.addAttribute("aboutMenu", fetchMenuFromDisk(about, usedLangCode))
        model.addAttribute("langCode", usedLangCode)
        model.addAttribute("topicKey", topicKey)
        model.addAttribute("topics", topics)
        // Add path and menu attributes based on servletPath
        val path = request.servletPath.removeSuffix("/")
        model.addAttribute("newSegment", NEW_SEGMENT)
        model.addAttribute("path", path)
        model.addAttribute("menu", fetchMenuFromDb(usedLangCode, false))
        if (!ctx.appConfig.auth || request.userPrincipal != null) //Fetch admin menu if required
            model.addAttribute("adminMenu", fetchMenuFromDb(usedLangCode, true))
        model.addAttribute("maxSummarySize", MAX_SUMMARY_SIZE)
        model.addAttribute("maxTitleSize", MAX_TITLE_SIZE)
        model.addAttribute("maxSegmentSize", MAX_SEGMENT_SIZE)
        // Build time if existing or current time. Forcing clear of css cache. Value in GUI at bottom.
        // Current time should only be used for selected local profiles, refer to BuildTimeValidation.kt
        val built = buildVersion(ctx.appConfig.buildTime)
        model.addAttribute("built", built)
        model.addAttribute("sessionMark", getSessionMark())
        return BlogParams(blog, oldLangCode, usedLangCode, action, topicKey, topics)
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
    // Ommitting parameter inn key and no messsage from Exception is logged
    // Evaluate in future if message should be sent directly e.g. for save in DB errors.
    protected fun handleRecoverableError(
        e: Exception,
        key: String,
        bindingResult: BindingResult,
    ) {
        logger.warn("${e.javaClass} key: error.$key ${e.message}")

        val args: Array<out Any> = arrayOf(e.message ?: "")
        bindingResult.reject("error.$key", args, "??error.$key?? ${e.message}")
    }

    protected fun authorize(owner: Owner?): Long {
        val blogOwnerId = owner?.userId ?: if (ctx.appConfig.auth)
            throw (NotAuthorizedException("not authorized for edit/save action"))
        else ctx.initService.getAdminId()
        return blogOwnerId
    }

    protected fun authorize(owner: Owner?, segment: String, langCode: String): BlogPrincipal {
        val blogOwnerId = authorize(owner)
        val validLangCode = getValidProjectLanguageCode(langCode)
        val blogId = ctx.blogService.findId(segment, blogOwnerId, validLangCode )
        return BlogPrincipal( blogOwnerId, blogId, validLangCode)
    }

    // finds hard coded documents and adds warning to model if not available in current languate.
    protected fun findDocument(menuItems: Array<MenuItem>, blogParams: BlogParams, segment: String?, model: Model): MenuItem? {
        val docIndex = getDocIndex(menuItems, blogParams.oldLangCode, blogParams.usedLangCode, segment)
        if (docIndex.error) {
            if (docIndex.index < 0) {
                return null
            } else {
                blogParams.oldLangCode?.let {
                    val otherLanguage = msg(ctx.messageSource, "warning.otherLanguage", it)
                    model.addAttribute("otherLanguage", otherLanguage)
                }
            }
        }
        return menuItems[docIndex.index]
    }

    private fun getSessionMark(): String {
        val fullId = ctx.httpSession.id
        return "[${fullId.substring(fullId.length -4)}]"
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
                topic.text = msg(ctx.messageSource, "topic." + topic.topicKey, languageCode)
            }
        }
        return topics
    }

    // Fetch menu items from database (Note: Only owner in this implementation)
    private fun fetchMenuFromDb(langCode: String, adminMenu: Boolean): List<MenuItem> {
        logger.debug("Fetch menu items by langCode=$langCode adminMenu=$adminMenu")
        val blogs = ctx.blogService.readMenu(langCode, adminMenu)
        return getMenuItems(
            menuItemOrig = blogs,
            submenuMinItems = SUBMENU_MIN_ITEMS,
            menuSplitSize = MENU_SPLIT_SIZE, ctx.messageSource)
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

    private fun noBlog(model: Model): BlogWithPosts? {
        model.addAttribute("blogHeadline", msg(ctx.messageSource, "noBlog"))
        return null
    }

}
