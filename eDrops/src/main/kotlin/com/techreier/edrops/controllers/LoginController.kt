package com.techreier.edrops.controllers

import com.techreier.edrops.config.logger
import com.techreier.edrops.service.DbService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes

const val LOGIN = "login"
const val LOGIN_DIR = LOGIN

@Controller
@RequestMapping(LOGIN_DIR)
class LoginController(private val dbService: DbService): BaseController(dbService)
{
    @GetMapping
    fun login(@RequestParam(required = false, name = "lang")  language: String?,
                 request: HttpServletRequest, model: Model): String {
        logger.info("Returning login page")
         val user = User()
         model.addAttribute("user", user)
         setCommonModelParameters(LOGIN, model, request, language)
        return LOGIN
    }

    @PostMapping
    fun verifyLogin(redirectAttributes: RedirectAttributes, @Valid @ModelAttribute("user") user: User,
                    bindingResult: BindingResult): String {
        logger.info("Handling login")
        if (user.password.length < 8) {
            bindingResult.addError(FieldError("user", "password",
                "Password 8 characters or more!"))
        }
        if (bindingResult.hasErrors()) {
            logger.info("error")
            return "login";
        }
        logger.info("no error")
        return "redirect:/$HOME_DIR"
    }

    data class User(var userid: String = "", var password: String = "")

}