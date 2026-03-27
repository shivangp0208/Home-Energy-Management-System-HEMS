package com.project.hems.site_manager_service.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class RequestLoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle start method={} uri={}", request.getMethod(), request.getRequestURI());
        try{
            Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
            if(authentication instanceof Jwt jwt){
                log.info("authenticated sub={} issuer={}", jwt.getSubject(), jwt.getIssuer());
            }else{
                log.info("no authentication found");
            }
            log.info("method type is {} ",request.getMethod());
        }catch (Exception e){
            log.error("Error in RequestLoggingInterceptor preHandle", e);
            e.printStackTrace();
            return false;
        }
        log.info("pre-filter is end");
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("post handle filter is run");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("request completion filter is run");
    }
}
