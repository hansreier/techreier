package com.sigmondsmart.edrops.repository

import com.sigmondsmart.edrops.domain.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

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
        languageRepo.save(blogData.norwegian)
        languageRepo.save(blogData.english)
        ownerRepo.save(blogData.blogOwner)
    }
}