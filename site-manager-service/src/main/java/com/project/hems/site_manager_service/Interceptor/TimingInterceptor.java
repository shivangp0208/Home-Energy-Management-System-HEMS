package com.project.hems.site_manager_service.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class TimingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime=System.currentTimeMillis();
        System.out.println("INTERCEPTOR-> "+request.getMethod() + "start time "+startTime);
        request.setAttribute("startTime",startTime);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long startTime=(Long) request.getAttribute("startTime");
        if(startTime!=null){
            long duration=System.currentTimeMillis()- startTime;
            System.out.println("INTERCEPTOR-> "+request.getMethod() + "duration "+duration);
        }

    }


}
