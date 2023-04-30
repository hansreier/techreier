package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.util.markdownToHtml
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class TestMarkdown {

    @Test
    fun `from markdown in readme to html`() {
        val html = markdownToHtml("readme.md")
        assertThat(html).contains("<h2") //Find better test
        assertThat(html).contains("</h2")
        logger.debug(html)
    }

    @Test
    fun `from non existing markdown in readme to html`() {
        val html = markdownToHtml("feil.md")
        assertThat(html).contains("not found")
        logger.debug(html)
    }
}