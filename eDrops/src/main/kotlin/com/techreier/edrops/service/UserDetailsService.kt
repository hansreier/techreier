package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Owner
import com.techreier.edrops.repository.BlogOwnerRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserDetailsService(
    private val blogOwnerRepository: BlogOwnerRepository
) : UserDetailsService {
    // Obtain details about user by a user name. Use DB service or hardcoded
    override fun loadUserByUsername(user: String): UserDetails {
        logger.info("Find user in database: $user")
        val blogOwner = blogOwnerRepository.findBlogOwnerByUsername(user)
            ?: throw UsernameNotFoundException("User not found with username: $user")
        return Owner(blogOwner)
    }
}

