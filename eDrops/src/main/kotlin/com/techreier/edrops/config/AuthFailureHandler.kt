package com.techreier.edrops.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
// https://www.baeldung.com/spring-security-custom-authentication-failure-handler
class AuthFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        logger.error("Auth error picked up")
        super.onAuthenticationFailure(request, response, exception)
        logger.info("Here")
        // Add custom logic here, such as setting a custom error message in the session
        request.session.setAttribute("errorMessage", "Wrong username or password")
    }
}