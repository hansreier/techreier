package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler

// https://www.baeldung.com/spring-security-custom-authentication-failure-handler
class AuthFailureHandler(private val forwardUrl: String) : ForwardAuthenticationFailureHandler(forwardUrl) {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        logger.warn("Authentication error: ${exception.message}") // TODO what about other error types?
        request.session.setAttribute("loginError", "loginError") //references language resource file
        response.sendRedirect(forwardUrl)
    }
}