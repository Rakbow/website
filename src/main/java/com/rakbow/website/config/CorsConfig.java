package com.rakbow.website.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Project_name: website
 * @Author: Rakbow
 * @Create: 2023-07-20 18:04
 * @Description: 跨域配置
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**") //允许跨域访问的路径
                .allowedOrigins("http://localhost:5173") //允许跨域访问的源域名
                .allowCredentials(true) // 是否发送跨域凭证
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH") // 允许请求的方法
                .allowedHeaders("*") // 允许的请求头
                .maxAge(3600);
    }
}
