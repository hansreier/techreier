package com.techreier.edrops.markdown

import com.techreier.edrops.config.MEDIA_URL_PATH
import com.techreier.edrops.config.logger
import com.techreier.edrops.controllers.ABOUT_DIR
import com.techreier.edrops.controllers.HOME_DIR
import com.techreier.edrops.data.EN
import com.techreier.edrops.data.NB
import com.techreier.edrops.data.Docs.about
import com.techreier.edrops.data.Docs.getDocIndex
import com.techreier.edrops.data.Docs.views
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class CMarkdownTest {

    private val  markdown = MarkdownC()

    @Test
    fun `from headings to html`() {
        val html = markdown.toHtml(HEADINGS)
        assertThat(html).doesNotContain("id=")
        assertThat(html)
            .contains("<h1>Testoppsett for Bloggsystem</h1>")
            .contains("<h2>Arkitektur og Design</h2>")
            .contains("<h3>Minimalistisk Implementasjon</h3>")
            .contains("<p>Nivå tre for å sikre at dybden i nodetreet blir riktig formatert under parsingen.</p>")
            .contains("<p>Til slutt kommer en helt vanlig, kort paragraf uten noe ekstra jåleri, akkurat som bestilt.</p>")
        logger.info("\n$html")
    }

    @Test
    fun `from secure markdown to html`() {
        val html = markdown.toHtml(SECURE)
        logger.info("\n$html")
        assertThat(html).contains("<p>Secure</p>", "<h2>")
    }

    @Test
    fun `from unsecure markdown to html`() {
        val html = markdown.toHtml(UNSECURE)
        logger.info("\n$html")
        assertThat(html).doesNotContain("<script>")
        assertThat(html).contains("<p>Unsecure</p>", "<h2>")
    }

    @Test
    fun `om meg to html Norwegian`() {
        val docIndex = getDocIndex(about, NB, NB, "reier")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.debug("Html: \n{}", inlineHtml)
        assertThat(inlineHtml.html).contains("Reier")
        assertEquals(NB, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `om meg to html English`() {
        val docIndex = getDocIndex(about, EN, EN, "reier")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.debug("html:\n{}", inlineHtml)
        assertThat(inlineHtml.html).contains("Reier")
        assertEquals(EN, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `tech to html Norwegian - use default language English`() {
        val docIndex = getDocIndex(about, NB, NB, "tech")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.debug("Html: \n{}", inlineHtml)
        assertThat(inlineHtml.html).contains("Technological")
        assertEquals(EN, inlineHtml.langCode)
        assertTrue(inlineHtml.warning)
    }

    @Test
    fun `markdown to html link and image - detailed verification`() {
        val docIndex = getDocIndex(about, EN, EN, "markdown")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.info("Html; \n$inlineHtml")
        assertThat(inlineHtml.html).contains("""<a href="../elpower""")
        assertThat(inlineHtml.html).contains("""<a href="tech""")
        assertThat(inlineHtml.html).contains("""<a href="https://openai.com/blog/chatgpt""")
        assertThat(inlineHtml.html).contains("""<a href="../energy""")
        assertThat(inlineHtml.html).contains("""<a href="/blog/politics""")
        assertThat(inlineHtml.html).contains("""<img src="../../images/pas.jpg" alt="My mascot PerSeter" title="Per Seter""")
        assertThat(inlineHtml.html).contains("$MEDIA_URL_PATH/cherries.jpg")
        assertEquals(EN, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `markdown to html table - detailed verification`() {
        val docIndex = getDocIndex(about, EN, EN, "markdown")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.info("Html; \n$inlineHtml")
        assertThat(inlineHtml.html).contains("""<td align="right">709037</td>""")
        assertThat(inlineHtml.html).contains("""<td align="right">4459</td>""")
        assertThat(inlineHtml.html).contains("""<td align="center">03</td>""")
        assertThat(inlineHtml.html).contains("""<td align="center">34</td>""")
        assertThat(inlineHtml.html).matches { html ->
            html.contains("""<td align="left">Oslo</td>""") || html.contains("""<td>Oslo</td>""")
        }

        assertThat(inlineHtml.html).contains("</table>")
        assertEquals(EN, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `markdown to html code block - final verification`() {
        val docIndex = getDocIndex(about, EN, EN, "markdown")
        assertThat(docIndex.index).isGreaterThan(-1)

        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.info("Html; \n${inlineHtml.html}")

        assertThat(inlineHtml.html).contains("<code>")
        assertThat(inlineHtml.html).contains("</code>")

        assertThat(inlineHtml.html).contains("fun isPrime")
        assertThat(inlineHtml.html).contains("Math.sqrt")
        assertThat(inlineHtml.html).contains("return true")

        assertThat(inlineHtml.html).contains("The page ends, and the answer is 42.")

        assertEquals(EN, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `markdown to one line code`() {
        val html = markdown.toHtml(CODE_LINE)
        logger.debug("Html: \n${html}")
        assertThat(html).contains("<code>fun isPrime</code>")
        assertThat(html).doesNotContain("<pre>")
        assertThat(html).contains("This is a")
        assertThat(html).contains("utility function inside a text line.")
    }

    @Test
    fun `markdown to horizontal rule`() {
        val html = markdown.toHtml(HORIZONTAL_RULE)
        logger.debug("Html: \n{}", html)
        assertThat(html).contains("<hr") // Overlever sanitizeren
        assertThat(html).contains("First paragraph of text.")
        assertThat(html).contains("Second paragraph after the line.")
    }

    @Test
    fun `Ringsaker to html English - cannot find in English`() {
        val docIndex = getDocIndex(views, NB, EN, "ringsaker")
        assertThat(docIndex.index).isGreaterThan(-1)
        assertTrue(docIndex.error)
        val doc = views[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, HOME_DIR)
        logger.debug("Html: \n{}", inlineHtml)
        assertThat(inlineHtml.html).contains("Ringsaker")
        assertEquals(NB, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `reier to html - wrong menu not found`() {
        val docIndex = getDocIndex(views, NB, NB, "reier")
        assertTrue(docIndex.error)
        assertEquals(-1, docIndex.index)
    }
}