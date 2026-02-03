package com.hems.project.Virtual_Power_Plant.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Slf4j
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        log.info("Vpp request interceptor invoked");
        ServletRequestAttributes attributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes!=null){
            String token=attributes.getRequest().getHeader("Authorization");
            if(token!=null && token.startsWith("Bearer ")){
                template.header("Authorization", token);
                log.debug("in request access token is attached {}",token);
            }else{
                log.warn("no token is avaliable for this request");
            }
        }
        log.info("vpp request interceptor is closed");
    }       
}

