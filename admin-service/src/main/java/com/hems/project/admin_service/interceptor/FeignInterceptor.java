package com.hems.project.admin_service.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Slf4j
public class FeignInterceptor implements RequestInterceptor {

    @Value("${jwt.system-token}")
    private String SYSTEM_TOKEN;

    @Override
    public void apply(RequestTemplate template) {

        log.debug("FeignInterceptor invoked. thread={}", Thread.currentThread().getName());

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            //use system token
            template.header("Authorization",SYSTEM_TOKEN);
            log.info("system token is use");
            return;

           // throw new IllegalStateException(
            //        "No request context available. Feign call must originate from an HTTP request.");
        }

        String token = attrs.getRequest().getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalStateException("Authorization header missing in request.");
        }

        template.header("Authorization", token);

        log.debug("Authorization header forwarded to downstream service");
    }
}