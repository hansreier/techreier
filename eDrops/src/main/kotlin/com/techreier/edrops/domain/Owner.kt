package com.techreier.edrops.domain

import org.springframework.http.HttpStatus
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ResponseStatusException

// A wrapper around blogOwner with Spring Boot UserDetails security added
class Owner(blogOwner: BlogOwner) : UserDetails {

    val id: Long = blogOwner.id
        ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No blogOwner exists")
    private val userName: String = blogOwner.username
    val userId: Long = blogOwner.id
        ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "No blogOwner id exists")
    private val password: String = blogOwner.password

    //Role based authorization, do it simple
    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
        val role = mutableSetOf<GrantedAuthority>()
        role.add(SimpleGrantedAuthority("read"))
        role.add(SimpleGrantedAuthority("write"))
        return role
    }

    override fun getPassword(): String = password

    override fun getUsername(): String = userName

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}