package com.techreier.edrops.repository

import com.techreier.edrops.domain.*
import jakarta.persistence.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder

abstract class Base {

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var entryRepo: BlogEntryRepository

    @Autowired
    lateinit var blogRepo: BlogRepository

    @Autowired
    lateinit var ownerRepo: BlogOwnerRepository

    @Autowired
    lateinit var languageRepo: LanguageRepository

    @Autowired
    lateinit var blogTextRepo: BlogTextRepository

    @Autowired
    lateinit var blogData: BlogData

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @BeforeEach
    fun setup() {
        blogData = BlogData(passwordEncoder)
        languageRepo.save(Norwegian)
        languageRepo.save(English)
        ownerRepo.save(blogData.blogOwner)
    }
}