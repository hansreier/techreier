package com.techreier.edrops.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

//Note there is a difference between @Controller and @RestController
//@Restcontroller does not work when referencing to thymeleaf templates
//@RestController is for web services.
@Controller
@RequestMapping("/")
public class EDropsController {
	private static final Logger logger = LoggerFactory.getLogger(EDropsController.class);

	@GetMapping({ "/java", "/greeting" })
	public String welcome() {
		logger.info("Welcome to Kotlin and Spring Boot world of tech");
		return "welcome";
	}
}

