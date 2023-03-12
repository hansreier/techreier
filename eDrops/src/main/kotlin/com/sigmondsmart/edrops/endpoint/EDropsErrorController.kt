package com.sigmondsmart.edrops.endpoint

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.domain.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


//Errors not picked up by error handler (e.g. 404 Not Found)
//Redirect to default website page can create recursion.
//It is better to redirect to an error page
//If disabled Springs default error handler is used to open error page
//This handler initializes som variables picked up by error page.
//TODO problematic with too complex error page, redesign.
//@Profile("notFOundProfile")
@Controller
class EDropsErrorController @Autowired private constructor(
    var errorAttributes: ErrorAttributes,
) : ErrorController, AbstractErrorController(errorAttributes) {
    //   class EDropsErrorController: ErrorController {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest, response: HttpServletResponse, model: Model): String {
        if (response.status == HttpServletResponse.SC_NOT_FOUND) return "redirect:/"
        val opts = getErrorAttributes(
            request, ErrorAttributeOptions.defaults()
                .including(ErrorAttributeOptions.Include.STACK_TRACE)
        ) //Problems override Spring config
        logger.info("Reier handles the error: $errorAttributes.")
        model.addAllAttributes(opts)
        model.addAttribute("languages", fetchLanguages())
        val langcode = model.getAttribute("langcode") ?: LocaleContextHolder.getLocale().language
        model.addAttribute("langcode", langcode)
        logger.info("Attributes added")
        return "error"
    }

    protected fun fetchLanguages(): MutableList<Language> {
        logger.info("Fetch languages (hard coded)")
        return mutableListOf(
            Language("lang.no", "nb-no"),
            Language("lang.eng", "en")
        )
    }
}