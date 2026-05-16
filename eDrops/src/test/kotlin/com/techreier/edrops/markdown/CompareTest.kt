package com.techreier.edrops.markdown

import org.junit.jupiter.api.Assertions.assertEquals

import com.techreier.edrops.config.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CompareTest {

    val markdownC = MarkdownC()
    val markdownF = MarkdownF()

    @Test
    fun `from headings to html`() {
        val htmlF = markdownF.toHtml(HEADINGS)
        val htmlC = markdownC.toHtml(HEADINGS)
        logger.info("Commonmark:\n$htmlC")
        logger.info("Flexmark:\n$htmlF")
        assertEquals(htmlF, htmlC)
        assertThat(htmlF).doesNotContain("id=")
    }
}