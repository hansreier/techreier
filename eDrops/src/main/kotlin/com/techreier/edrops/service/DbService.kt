package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.*
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItemDTO
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.repository.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
@Transactional
class DbService(
    private val ownerRepo: BlogOwnerRepository,
    private val blogRepo: BlogRepository,
    private val blogEntryRepo: BlogEntryRepository,
    private val languageRepo: LanguageRepository,
    private val topicRepo: TopicRepository,
) {

    fun readFirstBlog(blogOwnerId: Long): Blog? {
        logger.info("Read first blog")
        val blogOwner = ownerRepo.findByIdOrNull(blogOwnerId)
        val firstBlog = blogOwner?.blogs?.last()
        logger.info("blogOwner: $blogOwner")
        return firstBlog
    }

    fun readOwner(blogOwnerId: Long): BlogOwner? {
        logger.info("Read blog owner")
        //  return ownerRepo.findByIdOrNull(blogOwnerId)
        return ownerRepo.findById(blogOwnerId).orElse(null)
    }

    fun readBlog(blogId: Long?): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogId?.let { blogRepo.findAllById(it).orElse(null) }
    }

    fun findBlog(languageCode: String, segment: String): BlogDTO? {
        logger.info("Find blog by languageCode: $languageCode and segment: $segment")
        return blogRepo.findFirstBlogByTopicLanguageCodeAndSegment(languageCode, segment)?.let { blog ->
            return blog.id?.let { blogId ->
                BlogDTO(
                    blogId, blog.segment, blog.topic.topicKey, blog.topic.language.code,
                    blog.pos, blog.subject, blog.about
                )
            }
        }
    }

    fun readBlog(languageCode: String, blogId: Long): BlogDTO? {
        logger.info("Read blog with same language: $languageCode as blog with id $blogId")
        var blogDTO = null
        val blog = blogRepo.findById(blogId).orElse(null) //Finds current blog
        if (blog != null) {
            logger.debug("Current blog language code: {}, should be: {}", blog.topic.language.code, languageCode)
            if (blog.topic.language.code != languageCode) {
                val bs = blogRepo.findFirstBlogByTopicLanguageCodeAndSegment(languageCode, blog.segment)
                blogDTO = bs?.let { bs ->
                    bs.id?.let { id ->
                        return BlogDTO(
                            id, bs.segment, bs.topic.topicKey, bs.topic.language.code,
                            bs.pos, bs.subject, bs.about
                        )
                    }
                }

            } else {
                blogDTO = blog.id?.let { id ->
                    return BlogDTO(
                        id, blog.segment, blog.topic.topicKey, blog.topic.language.code,
                        blog.pos, blog.subject, blog.about
                    )
                }
            }
        }
        return blogDTO
    }


    //if language is changed, we try to fetch a blog with the new language and the same segment
    fun readBlogWithSameLanguage(blogId: Long, langCode: String?): Blog? {
        logger.info("Read blog with same language 2: $langCode as blog with id $blogId")
        var blogIdNew = blogId
        if (langCode != null) {
            val blog = blogRepo.findById(blogId).orElse(null) //Finds current blog
            if (blog != null) {
                logger.debug("The current blog is found with language.code ${blog.topic.language.code}, should be: $langCode")
                if (blog.topic.language.code != langCode) {
                    val blogSwitched =
                        blogRepo.findFirstBlogByTopicLanguageCodeAndSegment(langCode, blog.segment)
                    blogIdNew = blogSwitched?.id ?: blogId
                }
            }
        }
        return blogRepo.findAllById(blogIdNew).orElse(null)
    }

    fun readBlogs(languageCode: String): MutableSet<Blog> {
        logger.info("Read blogs with language: $languageCode")
        return blogRepo.findByTopicLanguageCode(languageCode)
    }

    fun readMenu(languageCode: String): List<MenuItemDTO> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }

    fun saveBlogEntry(blogId: Long?, blogEntryForm: BlogEntryForm) {
        logger.info("Saving blogEntry with id: ${blogEntryForm.id} segment: ${blogEntryForm.segment} blogId: $blogId")
        blogId?.let {
            val blog = blogRepo.findById(blogId).orElse(null)
            blog?.let { foundBlog ->
                val blogEntry = BlogEntry(
                    ZonedDateTime.now(), blogEntryForm.segment, blogEntryForm.title,
                    blogEntryForm.summary, foundBlog, blogEntryForm.id
                )
                if (blog.blogEntries.any { (it.segment == blogEntryForm.segment) && it.id != blogEntryForm.id }) {
                    throw DuplicateSegmentException("Segment: ${blogEntryForm.segment} is duplicate in blog ${blog.segment}")
                }

                blogEntryRepo.save(blogEntry)
            }
                ?: throw ParentBlogException("Blogentry ${blogEntryForm.segment} not saved, cannot read parent blog with id: $blogId")
        } ?: throw ParentBlogException("Blogentry ${blogEntryForm.segment} not saved, parent blog is detached")
    }

    fun deleteBlogEntry(blogId: Long?, blogEntryForm: BlogEntryForm) {
        logger.info("Deleting blogEntry with id: ${blogEntryForm.id} segment: ${blogEntryForm.segment} blogId: $blogId")
        blogEntryForm.id?.let { id ->
            blogEntryRepo.deleteById(id)
        } ?: logger.error("Blogentry not deleted, no id")
    }

    fun readLanguages(): MutableList<LanguageCode> {
        return languageRepo.findAll()
    }

    fun readTopics(languageCode: String): MutableList<Topic> {
        val topics = topicRepo.findAllByLanguageCodeOrderByPos(languageCode)
        return topics
    }
}