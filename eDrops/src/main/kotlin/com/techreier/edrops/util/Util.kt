package com.techreier.edrops.util

import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

// Return current zoned time
fun timeStamp(): ZonedDateTime = ZonedDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS)