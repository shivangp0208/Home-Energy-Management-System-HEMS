package com.project.hems.simulator_service.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

//jyare feign call thase koi biji service mate toh j aa interceptor work karse..
//and spring automatically ane run karse e request biji service ne mokle ena pehla
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

//so spring jyare biji service ne call karvanu che e request build kari ley toh last step ma ane
//run kare
/*
Controller (VPP)
   ↓
Service
   ↓
FeignClient method call
   ↓
Feign builds request
   ↓
FeignAuthInterceptor.apply()  ← HERE
   ↓
HTTP request sent to Site
 */
