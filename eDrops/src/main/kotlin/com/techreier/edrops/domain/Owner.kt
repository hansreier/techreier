package com.techreier.edrops.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

// A wrapper around blogOwner with Spring Boot UserDetails security added
class Owner(blogOwner: BlogOwner): UserDetails {

    val user = blogOwner

    //Role bases authorization, do it simple
    override fun getAuthorities(): MutableCollection<GrantedAuthority> {
       val role =  mutableSetOf<GrantedAuthority>()
        role.add(SimpleGrantedAuthority("read"))
        role.add(SimpleGrantedAuthority("write"))
       return role
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
       return user.username
    }

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