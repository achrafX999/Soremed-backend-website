// src/main/java/com/soremed/backend/Config/WebConfig.java
package com.soremed.backend.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${news.images.dir}")
    private String imagesDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Ajoutez aussi le mapping pour les images
        registry
                .addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
        registry
                .addMapping("/images/news/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:5173")
                .allowedMethods("GET","OPTIONS");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/images/news/**")
                .addResourceLocations("file:" + imagesDir + "/")
                .setCachePeriod(3600);
    }
}

