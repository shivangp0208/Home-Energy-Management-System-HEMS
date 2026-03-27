package com.project.hems.site_manager_service.Interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class RoleCheckInterceptor implements HandlerInterceptor {

    //controller ni request start thay ena pehla aa chale
    //so request passed via DispatchServlet and goes to controller eni vacche chale aa
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String role=request.getHeader("Role");
        if(role==null || !role.equals("ADMIN")){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied: Admins Only");
            return false;
        }

        System.out.println("INTERCEPTOR -> user role is valid"+role);
        return true;
    }

    //controller ni request puri thai jay ena pachi aa chale
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        System.out.println("INTERCEPTOR-> PostHandle: Controller Executed");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
