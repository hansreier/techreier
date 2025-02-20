package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.config.logger
import com.techreier.edrops.service.BlogService
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.i18n.SessionLocaleResolver

const val LOGIN = "login"
const val LOGIN_DIR = "/$LOGIN"

// https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
// https://www.baeldung.com/spring-security-extra-login-fields NOT THAT RELEVANT

@Controller
@RequestMapping(LOGIN_DIR)
class LoginController(
    blogService: BlogService,
    dbService: DbService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : BaseController(blogService, dbService, messageSource, sessionLocaleResolver, appConfig) {
    @GetMapping
    fun login(
        @RequestParam(required = false, name = "lang") language: String?,
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("Returning login page")
        val loginError = request.session.getAttribute("loginError") as String?
        request.session.setAttribute("loginError", null)
        model.addAttribute("loginError", loginError)
        val user = User()
        model.addAttribute("user", user)
        fetchBlogParams(model, request, response, language)
        return LOGIN
    }

/**
     *  Not in use, Spring Security handles this (disadvantage fields are blanked if wrong)
     *  TODO: Kept for future use
     *
     @PostMapping
     fun verifyLogin(redirectAttributes: RedirectAttributes, @Valid @ModelAttribute("user")
     user: User, language: String?, bindingResult: BindingResult, request: HttpServletRequest, model: Model): String {
     logger.info("Handling login")
     if (user.username.isEmpty()) {
     bindingResult.addError(FieldError("user", "username", msg("emptyUsername")))
     }
     if (user.password.length < 8) {
     bindingResult.addError(FieldError("user", "password", msg("shortPassword")))
     }
     if (bindingResult.hasErrors()) {
     logger.info("error")
     setCommonModelParameters(LOGIN, model, request, language)
     return LOGIN_DIR
     }
     logger.info("no error")
     return "redirect:/$HOME_DIR"
     }
     **/

    data class User(
        var username: String = "",
        var password: String = "",
    )
}
