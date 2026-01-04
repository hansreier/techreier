package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

const val LOGIN = "login"
const val LOGIN_DIR = "/$LOGIN"

// https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/form.html
// https://www.baeldung.com/spring-security-extra-login-fields NOT THAT RELEVANT

@Controller
@RequestMapping(LOGIN_DIR)
class LoginController(context: Context) : BaseController(context) {
    @GetMapping
    fun login(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
    ): String {
        logger.info("Returning login page")
        val session = request.getSession(false)
        val loginError =session?.getAttribute("loginError") as String?
        if (loginError != null) {
            model.addAttribute("loginError", loginError)
            session.removeAttribute("loginError")
        }
        val user = User()
        model.addAttribute("user", user)
        fetchBlogParams(model, request, response, null, false, true)
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
