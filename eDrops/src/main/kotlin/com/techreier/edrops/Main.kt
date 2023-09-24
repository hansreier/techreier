package com.techreier.edrops

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

// Class must always be named main, or else it will not be found by jvm, else add
// @file:JvmName("Main")
// SpringBootServletInitializer is essential when deploying to Tomcat.
@SpringBootApplication
class Main : SpringBootServletInitializer()
fun main(args: Array<String>) {
    runApplication<Main>(*args)
}
