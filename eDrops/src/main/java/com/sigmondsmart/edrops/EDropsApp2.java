package com.sigmondsmart.edrops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Profile;

/**
 * Point to class with the @SpringBootApplication annotation and app arguments
 * 
 * main starts Servlet 3 style Java config API that replaces web.xml
 * TODO Kept for reference, remove
 * Works fine, but why problems when moving this to Kotlin?
**/

/*
@SpringBootApplication
public class EDropsApp2 extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(EDropsApp2.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(EDropsApp2.class, args);
	}
} */
