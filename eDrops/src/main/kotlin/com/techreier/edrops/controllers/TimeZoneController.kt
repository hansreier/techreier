package com.techreier.edrops.controllers

import jakarta.servlet.http.HttpSession
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneId
import com.techreier.edrops.config.logger
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("timezone")
class TimeZoneController {

    //Receive time zone from client
    @PostMapping
    fun setTimeZone(@RequestBody body: Map<String, String>, session: HttpSession) {
        val timezone = body["timezone"]
        logger.info("Reier $timezone")
        timezone?.let {
            session.setAttribute("userTimeZone", ZoneId.of(it))
        }
    }
}