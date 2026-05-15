package com.techreier.edrops.util

import com.techreier.edrops.config.SANITIZER
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer


class CMarkdown: MarkdownEngine(), IMarkdown {

    // Commonmark implementation
    override fun toHtml(markdown: String): String {
        val exts: List<Extension> = listOf(
            TablesExtension.create(), AutolinkExtension.create(),
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
        if (SANITIZER) return sanitize(html) else return html
    }
}