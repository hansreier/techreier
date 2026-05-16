package com.techreier.edrops.markdown

import com.techreier.edrops.config.SANITIZER
import com.techreier.edrops.config.logger
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
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
        val parser: Parser = Parser.builder().extensions(exts).build()
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