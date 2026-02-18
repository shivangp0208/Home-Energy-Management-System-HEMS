//package com.hems.project.Virtual_Power_Plant.Config;
//
//import com.hems.project.Virtual_Power_Plant.interceptor.TokenIsAvailableOrNot;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//
//    @Autowired
//    private TokenIsAvailableOrNot tokenIsAvilableOrNot;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(tokenIsAvilableOrNot);
//    }
//}
