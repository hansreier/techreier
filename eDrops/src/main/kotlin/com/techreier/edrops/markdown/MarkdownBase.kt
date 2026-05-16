package com.techreier.edrops.markdown

import com.techreier.edrops.config.logger
import com.techreier.edrops.data.DEFAULT_LANGCODE
import com.techreier.edrops.data.MARKDOWN_EXT
import com.techreier.edrops.dto.MenuItem
import org.owasp.html.HtmlPolicyBuilder
import org.owasp.html.Sanitizers

abstract class MarkdownBase : IMarkdown {

    // https://owasp.org/www-project-java-html-sanitizer/
    fun sanitize(html: String): String {
        val policyTags = HtmlPolicyBuilder()
            .allowElements("p")
            .allowAttributes("align").onElements("p")
            .allowElements("img")
            .allowAttributes("title").onElements("img")
            .allowElements("hr")
            .toFactory()
        val policy =
            Sanitizers.BLOCKS
                .and(Sanitizers.FORMATTING)
                .and(Sanitizers.LINKS)
                .and(Sanitizers.TABLES)
                .and(Sanitizers.IMAGES)
                .and(policyTags)
        return policy.sanitize(html)
    }


    // Must do it this way with classLoader and streams to be able to read files in Docker and locally
// This implementation read file with file name constructed from segment and selected language code
// If not found it will look up a file named by default language code instead.
// What this means is that if Norwegian is selected, some text can be presented in English instead if not found.
    fun toHtml(menuItem: MenuItem, subDir: String = ""): InlineHtml {
        logger.debug("{}, subDir: {}", menuItem, subDir)
        var warning = false
        val classLoader = object {}.javaClass.classLoader
        val prefix = "static/markdown$subDir/${menuItem.segment}_"
        val fileName = prefix + menuItem.langCode + MARKDOWN_EXT
        val inputStream = classLoader.getResourceAsStream(fileName)
        var langCode = menuItem.langCode
        val markdown = inputStream?.bufferedReader().use { file ->
            file?.readText() ?: run {
                if (DEFAULT_LANGCODE != menuItem.langCode) {
                    warning = true
                    langCode = DEFAULT_LANGCODE
                    val defaultFileName = prefix + DEFAULT_LANGCODE + MARKDOWN_EXT
                    val defaultInputStream = classLoader.getResourceAsStream(defaultFileName)
                    defaultInputStream?.bufferedReader().use { defaultFile ->
                        defaultFile?.readText() ?: run {
                            langCode = ""
                            warning = false
                            "$fileName and $defaultFileName not found"
                        }
                    }
                } else "$fileName not found"
            }
        }

        return InlineHtml(toHtml(markdown), langCode, warning)
    }

    data class InlineHtml(val html: String, val langCode: String, val warning: Boolean)

}