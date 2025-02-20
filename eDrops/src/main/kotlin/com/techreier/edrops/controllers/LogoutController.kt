package com.techreier.edrops.controllers

import com.techreier.edrops.config.AppConfig
import com.techreier.edrops.dbservice.BlogService
import com.techreier.edrops.dbservice.GenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.i18n.SessionLocaleResolver

const val LOGOUT = "logout"
const val LOGOUT_DIR = "/$LOGOUT"

@Controller
@RequestMapping(LOGOUT_DIR)
class LogoutController(
    blogService: BlogService,
    genService: GenService,
    messageSource: MessageSource,
    sessionLocaleResolver: SessionLocaleResolver,
    appConfig: AppConfig,
) : BaseController(blogService, genService, messageSource, sessionLocaleResolver, appConfig) {
    @PostMapping("/logout")
    fun performLogout(
        authentication: Authentication,
        request: HttpServletRequest,
        response: HttpServletResponse,
    ): String {
        SecurityContextLogoutHandler().logout(request, response, authentication)
        return "redirect:/$HOME_DIR"
    }
}
