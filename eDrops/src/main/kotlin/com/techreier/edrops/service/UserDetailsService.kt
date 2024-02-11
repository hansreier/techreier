package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService : UserDetailsService {
    // Obtain details about user by a user name. Use DB service or hardcoded
    override fun loadUserByUsername(username: String): UserDetails {
        logger.info("Authorizing: $username")
        if (username == "reier") {
            logger.info("username found")
            val password = "hansreier"
            //TODO why include authentication logic here? page 77
            return User.withUsername(username) //Simple builder to define user name
                .password(password)
                .authorities("read", "write")
                .build()
        } else {
            logger.warn("$username does not exist")
            throw UsernameNotFoundException("User not found with username: $username")
        }
    }
}

