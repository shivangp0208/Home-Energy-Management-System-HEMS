//package com.hems.project.virtual_power_plant.interceptor;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Component
//@Slf4j
//public class TokenIsAvailableOrNot implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        System.out.println("FILTER---->>> pre proccessing");
//         String authorization = request.getHeader("Authorization");
//         log.info("header is {} ",authorization);
//         if (authorization!=null){
//             return true;
//         }else return false;
//    }
//}
