package com.techreier.edrops

import com.techreier.edrops.config.logger
import com.techreier.edrops.util.markdownToHtml
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
}