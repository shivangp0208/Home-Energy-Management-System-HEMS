package com.project.hems.SiteManagerService.config;

import com.project.hems.SiteManagerService.Interceptor.RoleCheckInterceptor;
import com.project.hems.SiteManagerService.Interceptor.TimingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//aa na vagar interceptor start nai thayy
@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final RoleCheckInterceptor roleCheckInterceptor;

    private final TimingInterceptor timingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //if /admin thi start thayy request toh j aa interceptor work karvu joiee
        registry.addInterceptor(roleCheckInterceptor).addPathPatterns("/admin/*");
        registry.addInterceptor(timingInterceptor).addPathPatterns("/*");
    }
}
