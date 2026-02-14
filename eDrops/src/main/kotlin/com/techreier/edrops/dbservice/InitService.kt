package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.data.Initial
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.PostState
import com.techreier.edrops.domain.Topic
import com.techreier.edrops.repository.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class InitService(
    private val languageRepo: LanguageRepository,
    private val ownerRepo: BlogOwnerRepository,
    private val blogRepo: BlogRepository,
    private val blogPostRepo: BlogPostRepository,
    private val topicRepo: TopicRepository,
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
            initial.base.topics.forEach { topic ->
                val existingTopic: Topic? = topicRepo.findByTopicKeyAndLanguageCode(topic.topicKey, topic.language.code)
                if (existingTopic != null) {
                    existingTopic.copyAttributes(topic)
                } else {
                    logger.info("new topic ${topic}")
                    topicRepo.save(topic) }
            }
            blogOwner = ownerRepo.findBlogOwnerByUsername(initial.blogOwner.username)
                ?: throw IllegalStateException("Initial blog owner not found")
            initial.blogOwner.blogs.forEach { blog ->
                val existingBlogs = blogRepo.findByTopicLanguageCodeAndSegment(blog.topic.language.code, blog.segment)
                if (existingBlogs.isEmpty()) {
                    blogRepo.save(blog)
                } else if (existingBlogs.size > 1) {
                    throw DuplicateKeyException("Duplicate blog ids: " + existingBlogs.map { it.id })
                }
                val blogId = existingBlogs[0].id ?: throw ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Blog ID kan ikke være null"
                )
                blog.blogPosts.forEach { post ->
                    val existingPosts = blogPostRepo.findByBlogIdAndSegmentAndState(blogId, post.segment, PostState.PUBLISHED.name)
                    if (existingPosts.isEmpty()) {
                        post.blog = existingBlogs[0]
                        blogPostRepo.save(post)
                    } else if (existingPosts.size > 1) {
                        throw DuplicateKeyException("Duplicate blogpost ids: " + existingPosts.map { it.id })
                    }
                    if (post.changed > existingPosts[0].changed) {
                        logger.info("new post: $post")
                        logger.info("old post: ${existingPosts[0]}")
                        blogPostRepo.save(existingPosts[0].copyAttributes(post))
                    }
                }
                if ((blog.changed > existingBlogs[0].changed)) {
                    logger.info("new blog: $blog")
                    logger.info("old blog: ${existingBlogs[0]}")
                    val topic = topicRepo.findByTopicKeyAndLanguageCode(blog.topic.topicKey, blog.topic.language.code)
                        ?: throw DataIntegrityViolationException("topic ${blog.topic.topicKey} not found")
                    existingBlogs[0].topic = topic
                    existingBlogs[0].copyAttributes(blog)
                }
            }
        }
        blogAdmin = blogOwner
        logger.info("Completed init")
    }
}