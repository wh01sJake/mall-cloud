package com.intelijake.mall.order.config;

import com.intelijake.mall.common.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration for Order Service
 * Configures JWT-based user context interceptor
 * 
 * @author Likun.Fang
 * @version 1.0
 * @since 2025-07-25
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserContextInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                    "/actuator/**",
                    "/error"
                );
    }
}
