package com.yrwcy.carpool.config;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     *注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

         //注册 Sa-Token 的路由拦截器
        registry.addInterceptor(new SaRouteInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/sign/**","/sendMessage","/destList","/formDetails","/getList","/byCategory",
                        "/query","/successForm");
    }
}
