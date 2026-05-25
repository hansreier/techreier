package com.techreier.edrops.markdown

import com.techreier.edrops.config.MEDIA_URL_PATH
import com.techreier.edrops.config.SANITIZER
import com.techreier.edrops.config.logger
import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Code
import org.commonmark.node.FencedCodeBlock
import org.commonmark.node.HtmlBlock
import org.commonmark.node.HtmlInline
import org.commonmark.node.Image
import org.commonmark.node.Link
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

//CommonMark
class MarkdownC: MarkdownBase(), IMarkdown {

    init {
        logger.info("Markdown engine Commonmark startet, sanitizer: ${SANITIZER}")
    }

    // Commonmark implementation
    override fun toHtml(markdown: String): String {
        logger.debug("Commonmark markdown to html, sanitizer: ${SANITIZER}")
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
                            logger.debug("Visitor - image path replaced: $origPath to: $MEDIA_URL_PATH/$origPath")
                            image.destination = "$MEDIA_URL_PATH/$origPath"
                        }
                    }
                    override fun visit(link: Link) {
                        val origPath = link.destination
                        if (origPath.contains(".md")) {
                            val segment = origPath.substringAfterLast("#", "")
                            val path = origPath.substringBeforeLast("#")
                                .replace(".md", "")
                                .replaceAfterLast("_", "")
                                .replace("_", "")
                                .replace("/home", "/") // home page no subpath
                            val newPath = if (segment.isEmpty()) path else "$path#$segment"
                            logger.debug("Visitor - Link path replaced: $origPath to: $newPath")
                            link.destination = newPath
                        }
                    }

                    // Dedicated math formulas in a , code with _ need to be escaped
                    override fun visit(fencedCodeBlock: FencedCodeBlock) {
                        if (fencedCodeBlock.info == "math:")  {
                            val mathRegex = Regex("""([_^])([a-zA-Z0-9]+)""")

                            val formattedHtml = fencedCodeBlock.literal
                                .replace(mathRegex) { match ->
                                    val type = match.groupValues[1]
                                    val value = match.groupValues[2]
                                    if (type == "_") "<sub>$value</sub>" else "<sup>$value</sup>"
                                }
                                .replace("\\_", "_")
                                .replace("\\^", "^")
                                .replace("\n", "<br>\n")

                            val htmlBlock = HtmlBlock()
                            htmlBlock.literal = "<div class=\"math-display\">$formattedHtml</div>"

                            fencedCodeBlock.insertAfter(htmlBlock)
                            fencedCodeBlock.unlink()
                        }
                    }

                    // Inline math formulas, code with _ need to be escaped
                    override fun visit(code: Code) {
                        val literal = code.literal
                        if (literal.startsWith("math:")) {
                            val formula = literal.removePrefix("math:")
                            val mathRegex = Regex("""([_^])([a-zA-Z0-9]+)""")

                            val formattedHtml = formula
                                .replace(mathRegex) { match ->
                                    val type = match.groupValues[1]
                                    val value = match.groupValues[2]
                                    if (type == "_") "<sub>$value</sub>" else "<sup>$value</sup>"
                                }
                                .replace("\\_", "_")
                                .replace("\\^", "^")

                            val htmlInline = HtmlInline()
                            htmlInline.literal = "<span class=\"math-inline\">$formattedHtml</span>"

                            code.insertAfter(htmlInline)
                            code.unlink()
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