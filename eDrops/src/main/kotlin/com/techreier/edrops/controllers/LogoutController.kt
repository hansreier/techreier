package com.techreier.edrops.controllers

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

const val LOGOUT = "logout"
const val LOGOUT_DIR = "/$LOGOUT"

@Controller
@RequestMapping(LOGOUT_DIR)
class LogoutController(context: Context) : Base(context) {
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
