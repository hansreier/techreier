package com.techreier.edrops.dbservice

import com.techreier.edrops.config.logger
import com.techreier.edrops.domain.Blog
import com.techreier.edrops.dto.BlogDTO
import com.techreier.edrops.dto.MenuItemDTO
import com.techreier.edrops.dto.toDTO
import com.techreier.edrops.repository.BlogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BlogService(
    private val blogRepo: BlogRepository,
) {
    // TODO not used. Consider removing
    fun readBlog(blogId: Long?): Blog? {
        logger.info("Read blog")
        // Does not fetch JPA annotations
        // val blog = blogRepo.findByIdOrNull(blogId)
        return blogId?.let { blogRepo.findWithEntriesById(it).orElse(null) }
    }

    // Read corrent blog based on segment,language code. Assumption: One owner
    fun readBlog(
        segment: String,
        oldLangCode: String?,
        langCode: String,
        entries: Boolean = false,
    ): BlogDTO? {
        logger.info("Read blog old LangCode: $oldLangCode langCode: $langCode, segment $segment, entries? $entries")

        // If blog is not found with current language, use the previous language code if different
        // This prevents annoying use of error page or redirect to home page, can fail if e.g. expired session.
        var blogLanguageDTO = blogRepo.getBlogWithLanguageCode(segment, langCode)

        if ((blogLanguageDTO == null) && (oldLangCode != null) && (oldLangCode != langCode))
            blogLanguageDTO = blogRepo.getBlogWithLanguageCode(segment, oldLangCode)

        blogLanguageDTO ?: return null

        val blog = if (entries)
                blogRepo.findWithEntriesById(blogLanguageDTO.id).orElse(null)
            else
                blogRepo.findById(blogLanguageDTO.id).orElse(null)
        return blog.toDTO(langCode, entries)
    }

    fun readMenu(languageCode: String): List<MenuItemDTO> {
        logger.info("Read menu from blog with language: $languageCode")
        return blogRepo.getMenuItems(languageCode)
    }

}
