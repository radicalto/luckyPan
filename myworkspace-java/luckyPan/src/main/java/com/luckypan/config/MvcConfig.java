package com.luckypan.config;

import com.luckypan.interceptor.loginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new loginInterceptor())
                .excludePathPatterns(
                        "/**",
                        "/login",
                        "/checkCode",
                        "/sendEmailCode",
                        "/register",
                        "/getAvatar/**",
                        "/logout",
                        "/resetPwd"
                );
    }
}
