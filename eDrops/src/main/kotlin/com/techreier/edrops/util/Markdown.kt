package com.techreier.edrops.util

import com.techreier.edrops.config.MEDIA_URL_PATH
import com.techreier.edrops.config.SANITIZER
import com.techreier.edrops.config.logger
import com.vladsch.flexmark.ast.Image
import com.vladsch.flexmark.ast.Link
import com.vladsch.flexmark.ext.autolink.AutolinkExtension
import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.ast.NodeVisitor
import com.vladsch.flexmark.util.ast.VisitHandler
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import com.vladsch.flexmark.util.sequence.BasedSequence

class Markdown : MarkdownEngine(), IMarkdown {

    private val headers = arrayOf("h1", "h2", "h3", "h4", "h5", "h6")

    var visitor: NodeVisitor = NodeVisitor(
        VisitHandler(Link::class.java) { link: Link -> visitLink(link) },
        VisitHandler(Image::class.java) { image: Image -> visitImage(image) }
    )

    // Visitor pattern, replace url path in md files
    // https://github.com/vsch/flexmark-java/blob/master/flexmark-java-samples/src/com/vladsch/flexmark/java/samples/FormatterWithMods.java
    fun visitLink(link: Link) {
        link.url = BasedSequence.of(replaceFileLinks(link.url.toString()))
        visitor.visitChildren(link)
    }

    fun visitImage(image: Image) {
        image.url = BasedSequence.of(replaceImageLinks(image.url.toString()))
        visitor.visitChildren(image)
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

    private fun replaceImageLinks(origPath: String): String {
        if (!origPath.startsWith("..")) {
            val newPath = "$MEDIA_URL_PATH/$origPath"
            logger.debug("Visitor - image path replaced: $origPath to: $newPath")
            return newPath
        } else return origPath
    }

    // Flexmark implementation of commonmark standardwith Github flovour
// https://github.com/vsch/flexmark-java/issues/92
    override fun toHtml(markdown: String): String {
        logger.debug("markdown to html, sanitizer: $SANITIZER")
        val options = MutableDataSet()
        options.setFrom(ParserEmulationProfile.GITHUB_DOC)
            .set(
                com.vladsch.flexmark.parser.Parser.EXTENSIONS,
                listOf<Extension>(
                    AutolinkExtension.create(),
                    TablesExtension.create(),
                    StrikethroughExtension.create(),
                )
            )

        options.set(TablesExtension.COLUMN_SPANS, false)
            .set(TablesExtension.MIN_HEADER_ROWS, 1)
            .set(TablesExtension.MAX_HEADER_ROWS, 1)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(TablesExtension.WITH_CAPTION, false)
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
            .set(HtmlRenderer.RENDER_HEADER_ID, true)

        // uncomment to convert soft-breaks to hard breaks
        // options.set(HtmlRenderer.SOFT_BREAK, "<br>\n");

        val parser = com.vladsch.flexmark.parser.Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()
        val document: Node = parser.parse(markdown)
        visitor.visit(document)
        val html = renderer.render(document)
        return if (SANITIZER) sanitize(html) else {
            logger.warn("Sanitizer turned off, use just for testing")
            html
        }
    }

}
