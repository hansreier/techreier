package com.techreier.edrops.markdown

import com.techreier.edrops.config.MEDIA_URL_PATH
import com.techreier.edrops.config.SANITIZER
import com.techreier.edrops.config.logger
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Image
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

//CommonMark
class MarkdownC: MarkdownBase(), IMarkdown {

    // Commonmark implementation
    override fun toHtml(markdown: String): String {
        logger.info("Commonmark markdown to html, sanitizer: ${SANITIZER}")
        val exts: List<Extension> = listOf(
            TablesExtension.create(),
            AutolinkExtension.create(),
            ImageAttributesExtension.create()
        )
        val parser: Parser = Parser.builder()
            .postProcessor { document ->
                document.accept(object : AbstractVisitor() {
                    override fun visit(image: Image) {
                        val origPath = image.destination
                        if (!origPath.startsWith("..")) {
                            logger.info("Visitor - image path replaced: $origPath to: $MEDIA_URL_PATH/$origPath")
                            image.destination = "$MEDIA_URL_PATH/$origPath"
                        }
                    }
                })
                document
            }
            .extensions(exts).build()
        val document = parser.parse(markdown)
        val renderer = HtmlRenderer.builder()
            .escapeHtml(false)
            // .attributeProviderFactory { ImageAttributeProvider() } Does not work on all attributes, bug?
            .extensions(exts)
            .build()
        val html = renderer.render(document)
        return if (SANITIZER) sanitize(html) else html
    }
}