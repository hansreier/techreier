package com.techreier.edrops.calc

data class ParseError(val key: String, val position: Int, val oper: String  = "")
