package com.techreier.edrops.markdown

interface IMarkdown {

    fun toHtml(markdown: String): String

}