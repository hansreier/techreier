package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.repository.BlogOwnerRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService(
    private val blogOwnerRepository: BlogOwnerRepository
) : UserDetailsService {
    // Obtain details about user by a user name. Use DB service or hardcoded
    override fun loadUserByUsername(user: String): UserDetails {
        logger.info("Authorizing: $user")
        val blogOwner = blogOwnerRepository.findBlogOwnerByUsername(user)
        if (blogOwner == null) {
            logger.warn("$user does not exist")
            throw UsernameNotFoundException("User not found with username: $user")
        }
        return Owner(blogOwner)
    }
}

