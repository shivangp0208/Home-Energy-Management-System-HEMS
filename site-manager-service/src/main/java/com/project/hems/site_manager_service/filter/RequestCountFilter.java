package com.project.hems.site_manager_service.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class RequestCountFilter implements Filter {

    private int count=0;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        System.out.println("FILTER -> RequestCounterFilter -> Preprocessing in filter");
        count++;
        request.setAttribute("requestNumber",count);

        HttpServletRequest servletRequest=(HttpServletRequest) request;
        String clientIp = servletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = servletRequest.getRemoteAddr();
        }

        System.out.println("Request coming from IP: " + clientIp);

        chain.doFilter(request,response);
        //response.getWriter().write("Request #"+ count + "processed");

        System.out.println("Filter -> RequestCountFilter -> Postprocessing in filter");

    }
}

