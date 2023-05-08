package com.sigmondsmart.edrops.util

import com.sigmondsmart.edrops.controllers.BaseController
import com.sigmondsmart.edrops.domain.DEFAULT_MARKDOWN_DIR
import com.sigmondsmart.edrops.domain.Docs
import com.sigmondsmart.edrops.domain.MARKDOWN_EXT
import org.commonmark.Extension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.owasp.html.Sanitizers
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.*

fun markdownToHtml(file: String? = null, text: String? = null): String {
    val logger = LoggerFactory.getLogger("markdownToHtml")

    try {
        val markdown = file?.let { File(file).readText(Charsets.UTF_8)} ?: text
        // https://owasp.org/www-project-java-html-sanitizer/
        val policy = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS).and(Sanitizers.TABLES)
        val exts: List<Extension> = Arrays.asList(TablesExtension.create())
        val parser: Parser = Parser.builder().extensions(exts).build()
        val document = parser.parse(markdown)
        val renderer = HtmlRenderer.builder().extensions(exts).build()
        val html = renderer.render(document)
        //  return html
        return policy.sanitize(html)
    } catch (e: FileNotFoundException) {
        logger.error("Not found: $file")
        return "$file not found"
    } catch (e: Exception) {
        logger.error("Not parsed: $file", e)
        return "$file not parsed"
    }
}

fun markdownToHtml(blogParams: BaseController.BlogParams): String {
    val doc = Docs.getDoc(blogParams.blogId)
    val lc = if (doc.ext) "_" + blogParams.langCode else ""
    return markdownToHtml(DEFAULT_MARKDOWN_DIR + "/" + doc.tag + lc + MARKDOWN_EXT)
}
