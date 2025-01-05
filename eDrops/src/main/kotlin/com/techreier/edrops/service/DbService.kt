package com.techreier.edrops.service

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.domain.BlogEntry
import com.techreier.edrops.domain.BlogOwner
import com.techreier.edrops.domain.LanguageCode
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.repository.BlogEntryRepository
import com.techreier.edrops.repository.BlogOwnerRepository
import com.techreier.edrops.repository.BlogRepository
import com.techreier.edrops.repository.LanguageRepository
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

    fun readBlog(blogId: Long): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogRepo.findAllById(blogId).orElse(null)
    }

    fun readBlog(languageCode: String, segment: String): Blog? {
        return blogRepo.findFirstBlogByLanguageAndSegment(LanguageCode("", languageCode), segment)
    }

    //if language is changed, we try to fetch a blog with the new language and the same segment
    // TODO, changing language on URL, blogId is not picked up and the logic is wrong
    // https://stackoverflow.com/questions/38803656/spring-thymeleaf-changing-locale-and-stay-on-the-current-page
    // Hidden input field cannot be used directly to transfer blogId when just changing URL parameter
    fun readBlogWithSameLanguage(blogId: Long, langCode: String?): Blog? {
        logger.info("Read blog with same language: $langCode as blog with id $blogId")
        var blogIdNew = blogId
        if (langCode != null) {
            val blog = blogRepo.findById(blogId).orElse(null) //Finds current blog
            if (blog != null) {
                logger.debug("The current blog is found with language.code ${blog.language.code}, should be: $langCode")
                if (blog.language.code != langCode) {
                    val blogSwitched =
                        blogRepo.findFirstBlogByLanguageAndSegment(LanguageCode("", langCode), blog.segment)
                    blogIdNew = blogSwitched?.id ?: blogId
                }
            }
        }
        return blogRepo.findAllById(blogIdNew).orElse(null)
    }

    fun readBlogs(languageCode: String): MutableSet<Blog> {
        logger.info("Read blogs with language: $languageCode")
        return blogRepo.findByLanguage(LanguageCode("", languageCode)) //TODO something goes wrong here
    }

    fun saveBlogEntry(blogId: Long?, blogEntryForm: BlogEntryForm) {
        logger.info("Saving blogEntry with id: ${blogEntryForm.id} segment: ${blogEntryForm.segment} blogId: $blogId")
        blogId?.let {
            val blog = blogRepo.findById(blogId).orElse(null)
            blog?.let { blog ->
                val blogEntry = BlogEntry(
                    ZonedDateTime.now(), blogEntryForm.segment, blogEntryForm.title,
                    blogEntryForm.summary, blog, blogEntryForm.id
                )
                blogEntryRepo.save(blogEntry)
            } ?: logger.error("Blogentry not saved, cannot read parent blog")
        } ?: logger.error("Blogentry not saved, parent blog is detached")
    }

    fun readLanguages(): MutableList<LanguageCode> {
        return languageRepo.findAll()
    }
}