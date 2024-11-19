package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler
import org.springframework.security.authentication.BadCredentialsException

// https://www.baeldung.com/spring-security-custom-authentication-failure-handler
class AuthFailureHandler(private val forwardUrl: String) : ForwardAuthenticationFailureHandler(forwardUrl) {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        logger.warn("Authentication error user: ${request.getParameter("username")} message: ${exception.message}")
        //References keywords in language resource files, for details refer to logs.
        val loginError = when (exception) {
            is BadCredentialsException -> "error.auth"
            else -> "error.login"
        }
        request.session.setAttribute("loginError", loginError)
        response.sendRedirect(forwardUrl)
    }
}