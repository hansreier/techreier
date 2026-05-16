package com.techreier.edrops.markdown

import org.junit.jupiter.api.Assertions.assertEquals

import com.techreier.edrops.config.logger
import com.techreier.edrops.controllers.ABOUT_DIR
import com.techreier.edrops.data.Docs.about
import com.techreier.edrops.data.Docs.getDocIndex
import com.techreier.edrops.data.EN
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

    @Test
    fun `markdown to html link and image - detailed verification`() {
        val docIndex = getDocIndex(about, EN, EN, "markdown")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val htmlF = markdownF.toHtml(doc, ABOUT_DIR)
        val htmlC = markdownC.toHtml(doc, ABOUT_DIR)
        assertEquals(htmlF, htmlC)
    }

    @Test
    fun `markdown to html table - detailed verification`() {
        val docIndex = getDocIndex(about, EN, EN, "markdown")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val htmlF = markdownF.toHtml(doc, ABOUT_DIR)
        val htmlC = markdownC.toHtml(doc, ABOUT_DIR)
        assertEquals(htmlF, htmlC)
    }

}