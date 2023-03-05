package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Language
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
abstract class BaseController(private val path: String) {

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
        model.addAttribute("path", path)
    }

    @PostMapping("/language")
    fun getLanguage(redirectAttributes: RedirectAttributes, code: String?): String {
        logger.info("valgt språkkode: $code")
        redirectAttributes.addFlashAttribute("langcode", code)
        return "redirect:$path?lang=$code"
       // return "redirect:${request.servletPath.removeSuffix("/language")}?lang=$code"
    }
}