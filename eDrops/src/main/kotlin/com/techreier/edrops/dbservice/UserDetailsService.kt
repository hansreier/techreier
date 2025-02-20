package com.techreier.edrops.dbservice

import com.techreier.edrops.domain.Owner
import com.techreier.edrops.repository.BlogOwnerRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Obtain details about user by checking in database
// Note that Spring Security by default replaces UsernameNotFoundException with BadCredentialsException
// Other exceptions results in error with stack trace
@Service
@Transactional
class UserDetailsService(
    private val blogOwnerRepository: BlogOwnerRepository,
) : UserDetailsService {
    override fun loadUserByUsername(user: String): UserDetails {
        val blogOwner =
            blogOwnerRepository.findBlogOwnerByUsername(user)
                ?: throw UsernameNotFoundException("User $user not found")
        return Owner(blogOwner)
    }
}
