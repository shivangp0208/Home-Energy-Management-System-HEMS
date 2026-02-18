package com.project.hems.SiteManagerService.config;

import com.project.hems.SiteManagerService.Interceptor.RoleCheckInterceptor;
import com.project.hems.SiteManagerService.Interceptor.TimingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//aa na vagar interceptor start nai thayy
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private RoleCheckInterceptor roleCheckInterceptor;

    @Autowired
    private TimingInterceptor timingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //if /admin thi start thayy request toh j aa interceptor work karvu joiee
        registry.addInterceptor(roleCheckInterceptor).addPathPatterns("/admin/*");
        registry.addInterceptor(timingInterceptor).addPathPatterns("/*");
    }
}
