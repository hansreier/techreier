package com.techreier.edrops.util

import com.techreier.edrops.domain.DEFAULT_LANGCODE
import com.techreier.edrops.dto.MenuItem
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import com.vladsch.flexmark.util.sequence.BasedSequence
import org.owasp.html.HtmlPolicyBuilder
import org.owasp.html.Sanitizers
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("com.techreier.edrops.util.markdownToHtml")

private val headers = arrayOf("h1", "h2", "h3", "h4", "h5", "h6")

var visitor: NodeVisitor = NodeVisitor(
    VisitHandler(Link::class.java) { link: Link -> visit(link) }
)

// Visitor pattern, replace url path in md files
// https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/FormatterWithMods.java
fun visit(link: Link) {
    link.url = BasedSequence.of(replaceFileLinks(link.url.toString()))
    visitor.visitChildren(link)
}

private fun replaceFileLinks(origPath: String): String {
    if (origPath.contains(".md")) {
        val segment = origPath.substringAfterLast("#", "")
        val path = origPath.substringBeforeLast("#")
            .replace(".md", "")
            .replaceAfterLast("_", "")
            .replace("_", "")
            .replace("/home", "/") //home page no subpath
        val newPath = if (segment.isEmpty()) path else "$path#$segment"
        logger.debug("Visitor - Link path replaced: $origPath to: $newPath")
        return newPath
    } else return origPath
}

// Flexmark implementation of commonmark standardwith Github flovour
// https://github.com/vsch/flexmark-java/issues/92
fun markdownToHtml(markdown: String, sanitizer: Boolean): String {
    logger.debug("markdown to html, sanitizer: $sanitizer")
    val options = MutableDataSet()
        .set(
            com.vladsch.flexmark.parser.Parser.EXTENSIONS,
            listOf<Extension>(
                AutolinkExtension.create(),
                TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create()
            )
        )

    options.set(TablesExtension.COLUMN_SPANS, false)
        .set(TablesExtension.MIN_HEADER_ROWS, 1)
        .set(TablesExtension.MAX_HEADER_ROWS, 1)
        .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
        .set(TablesExtension.WITH_CAPTION, false)
        .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)

    options.setFrom(ParserEmulationProfile.GITHUB_DOC)
    options.set(HtmlRenderer.RENDER_HEADER_ID, true)

    // uncomment to convert soft-breaks to hard breaks
    // options.set(HtmlRenderer.SOFT_BREAK, "<br>\n");

    val parser = com.vladsch.flexmark.parser.Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
    val document: Node = parser.parse(markdown)
    visitor.visit(document)
    val html = renderer.render(document)
    return if (sanitizer) sanitize(html) else {
        logger.warn("Sanitizer turned off, use just for testing")
        html
    }
}

// https://owasp.org/www-project-java-html-sanitizer/
fun sanitize(html: String): String {
    val policyTags = HtmlPolicyBuilder()
        .allowElements("p")
        .allowAttributes("align").onElements("p")
        .allowElements("img")
        .allowAttributes("title").onElements("img")
        .allowElements(*headers) //allow id on headers to support direct links to headlines on a page
        .allowAttributes("id").onElements(*headers)
        .toFactory()
    val policy =
        Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS)
            .and(Sanitizers.TABLES).and(Sanitizers.LINKS).and(Sanitizers.IMAGES).and(policyTags)
    return policy.sanitize(html)
}

// Must do it this way with classLoader and streams to be able to read files in Docker and locally
// This implementation read file with file name constructed from segment and selected language code
// If not found it will look up a file named by default language code instead.
// What this means is that if Norwegian is selected, some text can be presented in English instead if not found.
fun markdownToHtml(menuItem: MenuItem, subDir: String = ""): InlineHtml {
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

    return InlineHtml(markdownToHtml(markdown, true), langCode, warning)
}

data class InlineHtml(val html: String, val langCode: String, val warning: Boolean)


