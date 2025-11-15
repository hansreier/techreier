package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.data.Initial
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.repository.*
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class InitService(
    private val languageRepo: LanguageRepository,
    private val ownerRepo: BlogOwnerRepository,
    private val blogRepo: BlogRepository,
    private val blogPostRepo: BlogPostRepository,
    private val topicRepo: TopicRepository
) {

        lateinit var blogAdmin: BlogOwner

    fun saveInitialData(initial: Initial) {
        var blogOwner: BlogOwner
        if (ownerRepo.count() == 0L) {
            logger.info("Initialize empty database with data")
            languageRepo.saveAll(initial.base.languages)
            topicRepo.saveAll(initial.base.topics)
            blogOwner = ownerRepo.save(initial.blogOwner)
        } else {
            logger.info("Updating database with changed initial data")
            blogOwner = ownerRepo.findBlogOwnerByUsername(initial.blogOwner.username)
                ?: throw IllegalStateException("Initial blog owner not found")
            initial.blogOwner.blogs.forEach { blog ->
                val existingBlogs = blogRepo.findByTopicLanguageCodeAndSegment(blog.topic.language.code, blog.segment)
                if (existingBlogs.isEmpty()) {
                    blogRepo.save(blog)
                } else if (existingBlogs.size > 1) {
                    throw DuplicateKeyException("Duplicate blog ids: " + existingBlogs.map { it.id })
                }

                blog.blogPosts.forEach { post ->
                    val existingPosts = blogPostRepo.findByBlogAndSegment(existingBlogs[0], post.segment)
                    if (existingPosts.isEmpty()) {
                        post.blog = existingBlogs[0]
                        blogPostRepo.save(post)
                    } else if (existingPosts.size > 1) {
                        throw DuplicateKeyException("Duplicate blogpost ids: " + existingPosts.map { it.id })
                    }
                    if (post.changed > existingPosts[0].changed) {
                        blogPostRepo.save(existingPosts[0].copyAttributes(post))
                    }
                }
                logger.info("new blog: $blog")
                logger.info("old blog: ${existingBlogs[0]}")
                if ((blog.changed > existingBlogs[0].changed)) {
                    logger.info("data is changed")
                    blogRepo.save(existingBlogs[0].copyAttributes(blog))
                }
            }
        }
        blogAdmin = blogOwner
        logger.info("Completed init")
    }
}