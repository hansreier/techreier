package com.techreier.edrops.util

import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.gfm.tasklist.TaskListExtension
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.sequence.BasedSequence
import org.owasp.html.HtmlPolicyBuilder
import org.owasp.html.Sanitizers
import org.slf4j.LoggerFactory
import java.util.*


private val logger = LoggerFactory.getLogger("markdownToHtml")

var visitor: NodeVisitor = NodeVisitor(
    VisitHandler(Link::class.java) { link: Link -> visit(link) }
)

// Visitor pattern, replace url path in md files
// https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/FormatterWithMods.java
fun visit(link: Link) {
    if (link.url.endsWith(".md")) {
        val newLink = BasedSequence.of(link.url.toString().replace(".md", ""))
        logger.debug("Visitor - Link path replaced: ${link.url} to: $newLink")
        link.url = newLink
    }
    visitor.visitChildren(link)
}

// Flexmark implementation of commonmark standardwith Github flovour
// https://github.com/vsch/flexmark-java/issues/92
fun markdownToHtml(markdown: String, sanitizer: Boolean): String {
    logger.info("markdown2")
    val options = MutableDataSet()
        .set(
            com.vladsch.flexmark.parser.Parser.EXTENSIONS,
            Arrays.asList<com.vladsch.flexmark.util.misc.Extension>(
                com.vladsch.flexmark.ext.autolink.AutolinkExtension.create(),
                com.vladsch.flexmark.ext.tables.TablesExtension.create(),
                StrikethroughExtension.create(),
                TaskListExtension.create()))

    options.set(com.vladsch.flexmark.ext.tables.TablesExtension.COLUMN_SPANS, false)
        .set(com.vladsch.flexmark.ext.tables.TablesExtension.MIN_HEADER_ROWS, 1)
        .set(com.vladsch.flexmark.ext.tables.TablesExtension.MAX_HEADER_ROWS, 1)
        .set(com.vladsch.flexmark.ext.tables.TablesExtension.APPEND_MISSING_COLUMNS, true)
        .set(com.vladsch.flexmark.ext.tables.TablesExtension.DISCARD_EXTRA_COLUMNS, true)
        .set(com.vladsch.flexmark.ext.tables.TablesExtension.WITH_CAPTION, false)
        .set(com.vladsch.flexmark.ext.tables.TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)

    options.setFrom(ParserEmulationProfile.GITHUB_DOC)

    // uncomment to convert soft-breaks to hard breaks
    //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

    val parser = com.vladsch.flexmark.parser.Parser.builder(options).build()
    val renderer = com.vladsch.flexmark.html.HtmlRenderer.builder(options).build()
    val document: Node = parser.parse(markdown)
    visitor.visit(document)
    val html = renderer.render(document)
    if (sanitizer) return sanitize(html) else return html
}

// https://owasp.org/www-project-java-html-sanitizer/
fun sanitize(html: String): String {
    val policyTags = HtmlPolicyBuilder()
        .allowElements("p")
        .allowAttributes("align").onElements("p")
        .allowElements("img")
        .allowAttributes("title").onElements("img")
        .toFactory()
    val policy =
        Sanitizers.BLOCKS.and(Sanitizers.FORMATTING).and(Sanitizers.LINKS)
            .and(Sanitizers.TABLES).and(Sanitizers.LINKS).and(Sanitizers.IMAGES).and(policyTags)
    return policy.sanitize(html)
}

//Must do it this way with classLoader and streams to be able to read files in Docker and locally
fun markdownToHtml(doc: Doc): String {
    val lc = if (doc.ext) "_" + doc.language.code else ""
    val classLoader = object {}.javaClass.classLoader
    val fileName = "static/markdown/" + doc.tag + lc + MARKDOWN_EXT
    val inputStream = classLoader.getResourceAsStream(fileName)
    return markdownToHtml(
        inputStream?.bufferedReader().use { it?.readText() ?: "$fileName not found" }, true)
}




