package com.techreier.edrops.util

import com.techreier.edrops.config.logger
import com.techreier.edrops.controllers.ABOUT_DIR
import com.techreier.edrops.controllers.HOME_DIR
import com.techreier.edrops.domain.EN
import com.techreier.edrops.domain.NB
import com.techreier.edrops.util.Docs.about
import com.techreier.edrops.util.Docs.getDocIndex
import com.techreier.edrops.util.Docs.views
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.assertFalse

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
    @Test
    fun `from secure markdown to html`() {
        val html = markdownToHtml(SECURE, true)
        logger.info(html)
        assertThat(html).contains("<p>Secure</p>","<h2 ")
    }

    @Test
    fun `from unsecure markdown to html`() {
        val html = markdownToHtml(UNSECURE, true)
        logger.info(html)
        assertThat(html).doesNotContain("<script>")
        assertThat(html).contains("<p>Unsecure</p>","<h2 ")
    }

    @Test
    fun `om meg to html Norwegian`() {
        val docIndex = getDocIndex(about, NB, NB,"reier")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml =  markdownToHtml(doc, ABOUT_DIR)
        assertThat(inlineHtml.html).contains("Reier")
        assertEquals(NB, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `om meg to html English`() {
        val docIndex = getDocIndex(about, EN, EN, "reier")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml =  markdownToHtml(doc, ABOUT_DIR)
        logger.info("Html: \n$inlineHtml")
        assertThat(inlineHtml.html).contains("Reier")
        assertEquals(EN, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `tech to html Norwegian (use default language English)`() {
        val docIndex = getDocIndex(about, NB, NB, "tech")
        assertThat(docIndex.index).isGreaterThan(-1)
        val doc = about[docIndex.index]
        val inlineHtml =  markdownToHtml(doc, ABOUT_DIR)
        logger.info("Html: \n$inlineHtml")
        assertThat(inlineHtml.html).contains("Technological")
        assertEquals(EN, inlineHtml.langCode)
        assertTrue(inlineHtml.warning)
    }

    @Test
    fun `Ringsaker to html English (cannot find in English)`() {
        val docIndex = getDocIndex(views, NB, EN, "ringsaker")
        assertThat(docIndex.index).isGreaterThan(-1)
        assertTrue(docIndex.error)
        val doc = views[docIndex.index]
        val inlineHtml =  markdownToHtml(doc, HOME_DIR)
        logger.info("Html: \n$inlineHtml")
        assertThat(inlineHtml.html).contains("Ringsaker")
        assertEquals(NB, inlineHtml.langCode)
        assertFalse(inlineHtml.warning)
    }

    @Test
    fun `reier to html (wrong menu not found`() {
        val docIndex = getDocIndex(views, NB, NB,"reier")
        assertTrue(docIndex.error)
        assertEquals(-1, docIndex.index)
    }
}