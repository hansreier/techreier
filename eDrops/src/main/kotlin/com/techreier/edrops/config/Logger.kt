package com.techreier.edrops.config

import org.slf4j.LoggerFactory
import org.slf4j.Logger

// Generic Logback logger logger, replaces
// private val log = LoggerFactory.getLogger(MyController::class.java)
val <T : Any> T.logger : Logger
    get() = LoggerFactory.getLogger(this::class.java)