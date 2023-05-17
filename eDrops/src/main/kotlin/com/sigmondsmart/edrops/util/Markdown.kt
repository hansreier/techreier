package com.sigmondsmart.edrops.util

import com.sigmondsmart.edrops.controllers.BaseController
import com.sigmondsmart.edrops.domain.Docs
import com.sigmondsmart.edrops.domain.MARKDOWN_EXT
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.owasp.html.Sanitizers
import org.slf4j.LoggerFactory
import java.util.*


private val logger = LoggerFactory.getLogger("markdownToHtml")

fun markdownToHtml(markdown: String, sanitizer: Boolean): String {
    // https://owasp.org/www-project-java-html-sanitizer/
    val policy =
        Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS)
            .and(Sanitizers.TABLES).and(Sanitizers.LINKS).and(Sanitizers.IMAGES)
    val exts: List<Extension> = Arrays.asList(TablesExtension.create(), AutolinkExtension.create())
    val parser: Parser = Parser.builder().extensions(exts).build()
    val document = parser.parse(markdown)
    val renderer = HtmlRenderer.builder()
        .escapeHtml(false)
       // .attributeProviderFactory { ImageAttributeProvider() } Does not work on all attributes, bug?
        .extensions(exts)
        .build()
    val html = renderer.render(document)
    if (sanitizer) return policy.sanitize(html) else return html
}

//Must to it this way with classLoader and streams to be able to read files in Docker and locally
fun markdownToHtml(blogParams: BaseController.BlogParams): String {
    val doc = Docs.getDoc(blogParams.blogId)
    val lc = if (doc.ext) "_" + blogParams.langCode else ""
    val classLoader = object {}.javaClass.classLoader
    val fileName = "markdown/" + doc.tag + lc + MARKDOWN_EXT
    val inputStream = classLoader.getResourceAsStream(fileName)
    return markdownToHtml(
        inputStream?.bufferedReader().use { it?.readText() ?: "$fileName not found" }, false)
}




