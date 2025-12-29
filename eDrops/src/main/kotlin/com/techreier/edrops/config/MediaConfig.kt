package com.techreier.edrops.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class MediaConfig(private val appconfig: AppConfig) : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("$MEDIA_URL_PATH/**")
            .addResourceLocations(appconfig.mediaPath)

        logger.info("MediaConfig: Mapper /media/** til ${appconfig.mediaPath}")
    }
}