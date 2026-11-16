package com.techreier.edrops.calc

data class ParseError(val key: String, var position: Int, val oper: String  = "")
