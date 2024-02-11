package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthenticationService(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: BCryptPasswordEncoder
): AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication {
        logger.info("Authenticationg")
        TODO("Not yet implemented")
    }

    override fun supports(authentication: Class<*>?): Boolean {
        TODO("Not yet implemented")
    }
}