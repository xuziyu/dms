package com.caili.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: huey
 * @Desc: 实现静态资源的映射
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //实现静态资源的映射  workspaces/images/ 调用的时候这段路径可以直接没有 被映射了

    /**
     * //映射swg
     * //固定写法 file:/
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/META-INF/resources/")
                .addResourceLocations("file:/workspaces/images/");

    }
}
