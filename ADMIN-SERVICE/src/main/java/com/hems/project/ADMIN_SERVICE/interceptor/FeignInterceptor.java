package com.hems.project.ADMIN_SERVICE.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collection;

@Slf4j
@Component
public class FeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        log.info("FeignInterceptor invoked. thread={}", Thread.currentThread().getName());
        final Collection<String> requestVariables = template.getRequestVariables();
        System.out.println(requestVariables);

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            log.warn("RequestAttributes is NULL -> cannot forward Authorization header");
            return;
        }

        String token = attrs.getRequest().getHeader("Authorization");
        log.info("Authorization header = {}", token);

        if (token != null && token.startsWith("Bearer ")) {
            template.header("Authorization", token);
            log.info("Authorization forwarded to downstream");
        } else {
            log.warn("No Bearer token found in incoming request");
        }
    }
}
