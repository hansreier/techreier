package com.techreier.edrops.config

import com.techreier.edrops.data.Base
import com.techreier.edrops.data.Initial
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.repository.*
import com.techreier.edrops.util.buildVersion
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException

lateinit var blogAdmin: BlogOwner

@Configuration
@Profile("!test")
class Init(
    languageRepo: LanguageRepository,
    ownerRepo: BlogOwnerRepository,
    blogRepo: BlogRepository,
    blogPostRepo: BlogPostRepository,
    topicRepo: TopicRepository,
    appConfig: AppConfig,
) {
    init {
        val buildVersion = buildVersion(appConfig.buildTime, false)
        logger.info("App name: ${appConfig.appname} built: ${buildVersion}")
        if (!appConfig.auth) {
            logger.warn("Admin user auth is off, turn on and redeploy if production")
        }

        val base = Base()
        val initial = Initial(appConfig, base)

        if (ownerRepo.count() == 0L) {
            logger.info("Initialize empty database with data")
            languageRepo.saveAll(base.languages)
            topicRepo.saveAll(base.topics)
            val blogOwner = ownerRepo.save(initial.blogOwner)
            blogAdmin = blogOwner
        } else {
            logger.info("Updating database with changed initial data")
            val blogOwner = ownerRepo.findBlogOwnerByUsername(initial.blogOwner.username)
                ?: throw IllegalStateException("Initial blog owner not found")
            blogAdmin = blogOwner
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

                if ((blog.changed > existingBlogs[0].changed)) {
                    blogRepo.save(existingBlogs[0].copyAttributes(blog))
                }
            }
        }
        logger.info("Completed init")
    }
}

