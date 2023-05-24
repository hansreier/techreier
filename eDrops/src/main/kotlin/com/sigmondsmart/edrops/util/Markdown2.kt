package com.sigmondsmart.edrops.util

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.formatter.Formatter
import com.vladsch.flexmark.formatter.Formatter.FormatterExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import com.vladsch.flexmark.util.misc.Extension
import org.slf4j.LoggerFactory
import java.util.*
import java.util.List


private val logger = LoggerFactory.getLogger("markdownToHtml2")


fun markdownToHtml2(markdown: String, sanitizer: Boolean): String {
    logger.info("markdown2")
    val options = MutableDataSet()
        .setFrom(ParserEmulationProfile.GITHUB_DOC)
        .set(Parser.EXTENSIONS,
        Arrays.asList<Extension>(
            TablesExtension.create(),
            StrikethroughExtension.create()))

    options.set(TablesExtension.COLUMN_SPANS, false) //
        .set(TablesExtension.MIN_HEADER_ROWS, 1) //
        .set(TablesExtension.MAX_HEADER_ROWS, 1) //
        .set(TablesExtension.APPEND_MISSING_COLUMNS, true) //
        .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true) //
        .set(TablesExtension.WITH_CAPTION, false) //
        .set(TablesExtension.FORMAT_TABLE_APPLY_COLUMN_ALIGNMENT, true)
        .set(TablesExtension.FORMAT_TABLE_ADJUST_COLUMN_WIDTH, true)
    // uncomment to convert soft-breaks to hard breaks
    //options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");

    val parser = Parser.builder(options).build()
    val renderer = HtmlRenderer.builder(options).build()
    val document: Node = parser.parse(markdown)
    val html = renderer.render(document)
    if (sanitizer) return sanitize(html) else return html
}




