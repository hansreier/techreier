package com.techreier.edrops.util

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

private const val SECURE = """
## Functionality
Secure  
"""

private const val UNSECURE = """
## Functionality
This is a simple and limited Blog system.  
<script>  
  var s = "surprise!</script><script>alert('whoops!')</script>";  
</script>  
  
Unsecure
"""

class MarkdownTest {

    private val  markdown = Markdown()

    @Test
    fun `from secure markdown to html`() {
        val html = markdown.toHtml(SECURE, true)
        logger.debug(html)
        assertThat(html).contains("<p>Secure</p>", "<h2 ")
    }

    @Test
    fun `from unsecure markdown to html`() {
        val html = markdown.toHtml(UNSECURE, true)
        logger.debug(html)
        assertThat(html).doesNotContain("<script>")
        assertThat(html).contains("<p>Unsecure</p>", "<h2 ")
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

    //Todo add more here for new type of link
    @Test
    fun `markdown to html link and image - detailed verification`() {
        val docIndex = getDocIndex(about, EN, EN, "markdown")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml = markdown.toHtml(doc, ABOUT_DIR)
        logger.debug("Html: \n{}", inlineHtml)
        assertThat(inlineHtml.html).contains("""<a href="https://openai.com/blog/chatgpt""")
        assertThat(inlineHtml.html).contains("""<a href="../blogs/energy""")
        assertThat(inlineHtml.html).contains("""<img src="../../images/pas.jpg" alt="My mascot PerSeter" title="Per Seter""")
        assertThat(inlineHtml.html).contains("$MEDIA_URL_PATH/cherries.jpg")
        assertEquals(EN, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
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