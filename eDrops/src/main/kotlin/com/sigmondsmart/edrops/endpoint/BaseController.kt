package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Language
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.context.ServletContextAware
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest

abstract class BaseController : ServletContextAware {

    private var servletContext: ServletContext? = null
    override fun setServletContext(servletContext: ServletContext) {
        this.servletContext = servletContext
    }

    //Start with hard coding languages
    protected fun fetchLanguages(): MutableList<Language> {
        logger.info("Fetch languages (hard coded)")
        return mutableListOf(
            Language("lang.no", "nb-no"),
            Language("lang.eng", "en")
        )
    }

    protected fun setCommonModelParameters(model: Model, controllerPath: String) {
        model.addAttribute("languages", fetchLanguages())
        val langcode = model.getAttribute("langcode") ?: LocaleContextHolder.getLocale().language
        model.addAttribute("langcode", langcode)
        model.addAttribute("path", if (controllerPath == "/") "" else controllerPath)
    }

    @PostMapping("/language")
    fun getLanguage(model: Model, request: HttpServletRequest, redirectAttributes: RedirectAttributes, code: String?,
                    blogid: Long?): String {
        logger.info("valgt språkkode: $code path: ${request.servletPath} blogid: $blogid")
        redirectAttributes.addFlashAttribute("langcode", code)
        //Or else id blogid used to populate menu will not be preserved
        //Alternative to use hidden field is either cookie or store in session
        //If more state is needed to use Spring session (and store session in db) is recommended.
        redirectAttributes.addFlashAttribute("blogid", blogid)
        return "redirect:${controllerPath(request.servletPath)}?lang=$code"
    }

    private fun controllerPath(currentPath: String): String {
        return currentPath.replaceAfterLast("/", "").removeSuffix("/")
    }
}