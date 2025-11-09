package com.techreier.edrops.config

import com.techreier.edrops.data.Base
import com.techreier.edrops.data.Initial
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.repository.*
import com.techreier.edrops.util.buildVersion
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

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
                val existingBlog = blogRepo.findByTopicLanguageCodeAndSegment(blog.topic.language.code, blog.segment)
                if (existingBlog == null) {
                    blogRepo.save(blog)
                } else {
                    blog.blogPosts.forEach { post ->
                        val existingPost = blogPostRepo.findByBlogAndSegment(existingBlog, post.segment)
                        if (existingPost == null) {
                            post.blog = existingBlog
                            blogPostRepo.save(post)
                        } else {
                            if (post.changed > existingPost.changed) {
                                blogPostRepo.save(existingPost.copyAttributes(post))
                            }
                        }

                    }
                    if ((blog.changed > existingBlog.changed)) {
                        blogRepo.save(existingBlog.copyAttributes(blog))
                    }
                }
            }
            logger.info("Completed init")
        }
    }
}
