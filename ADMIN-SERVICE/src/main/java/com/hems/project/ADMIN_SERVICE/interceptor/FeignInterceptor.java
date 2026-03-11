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
//    @Override
//    public void apply(RequestTemplate template) {
//        log.info("FeignInterceptor invoked. thread={}", Thread.currentThread().getName());
//        final Collection<String> requestVariables = template.getRequestVariables();
//        System.out.println(requestVariables);
//
//        ServletRequestAttributes attrs =
//                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//
//        if (attrs == null) {
//            log.warn("RequestAttributes is NULL -> cannot forward Authorization header");
//            return;
//        }
//
//        String token = attrs.getRequest().getHeader("Authorization");
//        log.info("Authorization header = {}", token);
//
//        if (token != null && token.startsWith("Bearer ")) {
//            template.header("Authorization", token);
//            log.info("Authorization forwarded to downstream");
//        } else {
//            log.warn("No Bearer token found in incoming request");
//        }
//    }

    @Override
    public void apply(RequestTemplate template) {

        log.info("FeignInterceptor invoked. thread={}", Thread.currentThread().getName());

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // Case 1: Scheduler / Background thread
        if (attrs == null) {
            log.warn("RequestAttributes NULL -> using system token");

            template.header(
                    "Authorization",
                    "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IkxjY2l6R3dZMGRNNnpvbmxYZlRrbiJ9.eyJodHRwOi8vaGVtcy5jb20vZW1haWwiOiJqaWxsc3BhdGVsMjAwNEBnbWFpbC5jb20iLCJodHRwczovL2hlbXMuZXhhbXBsZS5jb20vc2lkIjoiYzE2MWI3NzYtZDU1NS00ZjEwLWIyNzctOWZlZGUzMDc0YmZlIiwiaXNzIjoiaHR0cHM6Ly9kZXYtMHg1YmcxdXkxZWdpejR5MC51cy5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8Njk4NDU4YTFkMTM3NGNkZjQ1MWE5N2MxIiwiYXVkIjpbImh0dHBzOi8vYXBpLm1mYTAxLmNvbSIsImh0dHBzOi8vZGV2LTB4NWJnMXV5MWVnaXo0eTAudXMuYXV0aDAuY29tL3VzZXJpbmZvIl0sImlhdCI6MTc3MzA1MDEzNywiZXhwIjoxNzczMTM2NTM3LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIG9mZmxpbmVfYWNjZXNzIiwiYXpwIjoiZXNZS1lHUG5QdDdxdmtuOGdaN1F0MVdJNEljTmtFeDgiLCJwZXJtaXNzaW9ucyI6WyJ2cHA6d3JpdGUiXX0.nr0Scu95gGNK4cMne7Y3hy6sosWHTLnF7t6kdQVaa6uCgSG5ERP6OibDQZWtjJJNqi4mwsvEzJVFr2ObQsV4UOrbH41Lw93gkTt7_PNyqp2FcndED_eUFZHpBley9wpygqN34kj5LAZyabcRsvnX3dxWgwdDvDnvwpSLCY7mTUR0_M5kIbqINjfBWDniv8w1CvO__MmnWJNPzp5besWltwwjISy6lFNHQpbWvVagSz6r-AvCto3M-5wh-hmLvLLPi_z2weqB4C0fHeSPWHMHS9P9_TtFbm2bztf-Guk758jZZJXsjdde7IpFjPTVzX6G2G5yLVhQOpM2lzWkUqVAKQ"
            );

            return;
        }

        // Case 2: Normal HTTP request
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
