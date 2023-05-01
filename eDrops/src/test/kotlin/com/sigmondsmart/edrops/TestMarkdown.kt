package com.sigmondsmart.edrops

import com.sigmondsmart.edrops.config.logger
import com.sigmondsmart.edrops.util.markdownToHtml
import org.assertj.core.api.Assertions.assertThat
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

class TestMarkdown {
    @Test
    fun `from markdown in readme file to html`() {
        val html = markdownToHtml("readme.md")
        logger.debug(html)
        assertThat(html).contains("<h2") //Find better test
        assertThat(html).contains("</h2")
    }

    @Test
    fun `from non existing markdown in readme to html`() {
        val html = markdownToHtml("feil.md")
        logger.debug(html)
        assertThat(html).contains("not found")
    }

    @Test
    fun `from secure markdown to html`() {
        val html = markdownToHtml(text = SECURE)
        logger.info(html)
        assertThat(html).contains("<p>Secure</p>","<h2>")
    }

    @Test
    fun `from unsecure markdown to html`() {
        val html = markdownToHtml(text = UNSECURE)
        logger.info(html)
        assertThat(html).doesNotContain("<script>")
        assertThat(html).contains("<p>Unsecure</p>","<h2>")
    }
}