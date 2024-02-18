package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class AuthenticationService(
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder
): AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        logger.info("Authenticating")
        val name = authentication.name
        val password = authentication.credentials.toString()
        val userDetails = userDetailsService.loadUserByUsername(name)
        return checkPassword(userDetails, password, passwordEncoder)
    }

    // TODO Always true OK?
    override fun supports(authentication: Class<*>?): Boolean {
        return true
    }

    private fun checkPassword(userDetails: UserDetails, rawPassword: String, encoder: PasswordEncoder): Authentication {
        logger.info("checking password: ${userDetails.password}")
        if (encoder.matches(rawPassword, userDetails.password)) return UsernamePasswordAuthenticationToken(
            userDetails.username, userDetails.password, userDetails.authorities
        ) else {
            throw BadCredentialsException("Bad credentials for user: ${userDetails.username}")
        }
    }
}