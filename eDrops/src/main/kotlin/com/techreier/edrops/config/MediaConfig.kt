package com.techreier.edrops.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MediaConfig(private val appconfig: AppConfig, private val resourcePatternResolver: ResourcePatternResolver) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val path = appconfig.mediaPath
        val resource = resourcePatternResolver.getResource(path)

        if (!resource.exists()) {
            logger.error("Mapping /media/** to ${path} fails. Pictures and other media files probably not available")
        } else {
            logger.info("Mapping /media/** to ${path} with success")
        }

        registry.addResourceHandler("$MEDIA_URL_PATH/**")
            .addResourceLocations(path)
    }
}