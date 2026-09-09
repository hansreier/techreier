package com.techreier.edrops.graph

import com.techreier.edrops.config.TOLERANCE

//Calculate nice numbers for graph intervals given normalized segment size (value between ticks)
fun niceNumber(normalized: Double): NiceRange {

    if ((normalized < (1 - TOLERANCE)  ) || (normalized > (10 + TOLERANCE) )) {
        throw IllegalStateException("Input data to nice number algorithm is not normalized in range (1..10)")
    }
    val niceRanges = listOf<NiceRange>(
        NiceRange(0.9, 1.0, 4),
        NiceRange( 1.5, result = 2.0, subTics = 4),
        NiceRange( 2.3, result = 2.5, subTics = 5),
        NiceRange( 3.2, result = 4.0, subTics = 4),
        NiceRange( 4.4, result = 5.0, subTics = 5),
        NiceRange( 7.0, result = 8.0, subTics = 4),
        NiceRange( 9.0, result = 10.0, subTics = 4)
    )

    var i = 0
    val length = niceRanges.size

    while ((i < length) && (normalized >= niceRanges[i].limit)) {
        i++
    }

    return niceRanges[i-1]

}

data class NiceRange(val limit: Double, val result: Double, val subTics: Int)