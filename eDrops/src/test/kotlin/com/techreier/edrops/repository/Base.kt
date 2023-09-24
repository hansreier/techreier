package com.techreier.edrops.repository

import com.techreier.edrops.domain.*
import jakarta.persistence.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
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

    lateinit var blogData: BlogData

    @BeforeEach
    fun setup() {
        blogData = BlogData()
        languageRepo.save(Norwegian)
        languageRepo.save(English)
        ownerRepo.save(blogData.blogOwner)
    }
}