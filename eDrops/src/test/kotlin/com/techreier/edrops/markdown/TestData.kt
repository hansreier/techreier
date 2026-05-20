package com.techreier.edrops.markdown

const val SECURE = """
## Functionality
Secure  
"""

const val UNSECURE = """
## Functionality
This is a simple and limited Blog system.  
<script>  
  var s = "surprise!<\/script><script>alert('whoops!')<\/script>";  
</script>  
  
Unsecure
"""

const val HEADINGS = """
# Testoppsett for Bloggsystem
Dette er en kort introduksjonstekst for å se at parseren håndterer tekst helt i starten av et dokument før neste seksjon.

## Arkitektur og Design
Her tester vi nivå to. Denne skal være helt ren, uten uønskede ID-attributter eller autogenererte lenker fra overivrige biblioteker.

### Minimalistisk Implementasjon
Nivå tre for å sikre at dybden i nodetreet blir riktig formatert under parsingen.

Til slutt kommer en helt vanlig, kort paragraf uten noe ekstra jåleri, akkurat som bestilt.
"""

const val CODE_LINE = "This is a `fun isPrime` utility function inside a text line."

const val HORIZONTAL_RULE = """
First paragraph of text. 

---

Second paragraph after the line.
"""

val markdownC = MarkdownC()
val markdownF = MarkdownF()