package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.logger
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.junit.jupiter.api.Test
import java.io.File
import org.assertj.core.api.Assertions.assertThat

class TestMarkdown {

    @Test
    fun `from markdown in readme to html`() {
        val readme = File("readme.md").readText(Charsets.UTF_8)
        val parser: Parser = Parser.builder()
            .build()
        val document = parser.parse(readme)
        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(document)
        assertThat(html).contains("<h2") //Find better test
        assertThat(html).contains("</h2")
        logger.info("Parsed $html")
    }
}