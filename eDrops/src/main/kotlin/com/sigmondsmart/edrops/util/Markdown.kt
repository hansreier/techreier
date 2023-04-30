package com.sigmondsmart.edrops.util

import org.commonmark.Extension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.*


fun markdownToHtml(file: String): String {
    val logger = LoggerFactory.getLogger("markdownToHtml")
    try {
        val readme = File(file).readText(Charsets.UTF_8)
        val exts: List<Extension> = Arrays.asList(TablesExtension.create())
        val parser: Parser = Parser.builder().extensions(exts).build()
        val document = parser.parse(readme)
        val renderer = HtmlRenderer.builder().extensions(exts).build()
        return renderer.render(document)
    } catch (e: FileNotFoundException) {
        logger.error("Not found: $file")
        return "$file not found"
    } catch (e: Exception) {
        logger.error("Not parsed: $file", e)
        return "$file not parsed"
    }
}
