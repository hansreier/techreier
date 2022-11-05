@file:JvmName("Main")

package com.sigmondsmart.edrops

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

@SpringBootApplication
class EDropsAppMain : SpringBootServletInitializer()
fun main(args: Array<String>) {
    runApplication<EDropsAppMain>(*args)
}
