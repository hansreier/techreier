package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Language
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import javax.servlet.http.HttpServletRequest
abstract class BaseController(private var path: String) {

    //Start with hard coding languages
    protected fun fetchLanguages(): MutableList<Language> {
        logger.info("Fetch languages (hard coded)")
        return mutableListOf(
            Language("lang.no","nb-no"),
            Language("lang.eng","en")
        )
    }

    protected fun setCommonModelParameters(model: Model) {
        model.addAttribute("languages",fetchLanguages())
        val langcode = model.getAttribute("langcode") ?: LocaleContextHolder.getLocale().language
        model.addAttribute("langcode", langcode )
    }

    @PostMapping("/language")
    fun getLanguage(request: HttpServletRequest, redirectAttributes: RedirectAttributes, code: String?): String {
        logger.info("valgt språkkode: $code")
        redirectAttributes.addFlashAttribute("langcode", code)
        logger.info("Path: ${request.servletPath} ContextPath: $path  URI: ${request.requestURL}")
        return "redirect:$path?lang=$code"
    }
}