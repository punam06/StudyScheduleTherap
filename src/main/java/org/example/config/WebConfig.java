package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve static files from static-site directory
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "file:static-site/")
                .setCachePeriod(0); // Disable caching for development
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Map root to index.html
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
