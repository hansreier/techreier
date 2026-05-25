package com.techreier.edrops.markdown

import org.junit.jupiter.api.Assertions.assertEquals

import com.techreier.edrops.config.logger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CompareTest {

    @Test
    fun `from headings to html`() {
        val htmlF = markdownF.toHtml(HEADINGS)
        val htmlC = markdownC.toHtml(HEADINGS)
        logger.info("Commonmark:\n$htmlC")
        logger.info("Flexmark:\n$htmlF")
        assertEquals(htmlF, htmlC)
        assertThat(htmlF).doesNotContain("id=")
    }


    @Test
    fun `markdown to one line code`() {
        val htmlF = markdownF.toHtml(CODE_LINE)
        val htmlC = markdownF.toHtml(CODE_LINE)
        assertEquals(htmlF, htmlC)
    }

    @Test
    fun `markdown to horizontal rule`() {
        val htmlF = markdownF.toHtml(HORIZONTAL_RULE)
        val htmlC = markdownF.toHtml(HORIZONTAL_RULE)
        assertEquals(htmlF, htmlC)
    }

}