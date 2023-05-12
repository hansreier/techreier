package com.sigmondsmart.edrops.util

import com.sigmondsmart.edrops.controllers.BaseController
import com.sigmondsmart.edrops.domain.Docs
import com.sigmondsmart.edrops.domain.MARKDOWN_EXT
import org.commonmark.Extension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.owasp.html.Sanitizers
import org.slf4j.LoggerFactory
import java.util.*

private val logger = LoggerFactory.getLogger("markdownToHtml")

fun markdownToHtml(markdown: String): String {
    // https://owasp.org/www-project-java-html-sanitizer/
    val policy = Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS).and(Sanitizers.TABLES)
    val exts: List<Extension> = Arrays.asList(TablesExtension.create())
    val parser: Parser = Parser.builder().extensions(exts).build()
    val document = parser.parse(markdown)
    val renderer = HtmlRenderer.builder().extensions(exts).build()
    val html = renderer.render(document)
    //  return html
    return policy.sanitize(html)
}

//Must to it this way to be able to read files in Docker and locally
fun markdownToHtml(blogParams: BaseController.BlogParams): String {
    val doc = Docs.getDoc(blogParams.blogId)
    val lc = if (doc.ext) "_" + blogParams.langCode else ""
    val classLoader = object {}.javaClass.classLoader
    val inputStream = classLoader.getResourceAsStream("markdown/" + doc.tag + lc + MARKDOWN_EXT)
    return markdownToHtml(inputStream?.bufferedReader(Charsets.UTF_8).use { it?.readText() ?: "File source not found" })
}




