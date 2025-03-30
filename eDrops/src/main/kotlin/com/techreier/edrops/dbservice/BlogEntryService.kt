package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.BlogEntry
import com.techreier.edrops.exceptions.DuplicateSegmentException
import com.techreier.edrops.exceptions.ParentBlogException
import com.techreier.edrops.forms.BlogEntryForm
import com.techreier.edrops.repository.BlogEntryRepository
import com.techreier.edrops.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Service
@Transactional
class BlogEntryService(
    private val blogEntryRepo: BlogEntryRepository,
    private val blogRepo: BlogRepository,
) {
    fun save(
        blogId: Long?,
        blogEntryForm: BlogEntryForm,
    ) {
        logger.info("Saving blogEntry with id: ${blogEntryForm.id} segment: ${blogEntryForm.segment} blogId: $blogId")
        blogId?.let {
            val blog = blogRepo.findById(blogId).orElse(null)
            blog?.let { foundBlog ->
                val blogEntry =
                    BlogEntry(
                        ZonedDateTime.now(),
                        blogEntryForm.segment,
                        blogEntryForm.title,
                        blogEntryForm.summary,
                        foundBlog,
                        blogEntryForm.id,
                    )
                if (blog.blogEntries.any { (it.segment == blogEntryForm.segment) && (it.id != blogEntryForm.id) }) {
                    throw DuplicateSegmentException("Segment: ${blogEntryForm.segment} is duplicate in blog ${blog.segment}")
                }
                blogEntryRepo.save(blogEntry)
            }
                ?: throw ParentBlogException("Blogentry ${blogEntryForm.segment} not saved, cannot read parent blog with id: $blogId")
        } ?: throw ParentBlogException("Blogentry ${blogEntryForm.segment} not saved, parent blog is detached")
    }

    fun delete(
        blogId: Long?,
        blogEntryForm: BlogEntryForm,
    ) {
        logger.info("Deleting blogEntry with id: ${blogEntryForm.id} segment: ${blogEntryForm.segment} blogId: $blogId")
        blogEntryForm.id?.let { id ->
            blogEntryRepo.deleteById(id)
        } ?: logger.error("Blogentry not deleted, no id")
    }
}
