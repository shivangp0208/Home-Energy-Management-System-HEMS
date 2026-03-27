package com.project.hems.site_manager_service.filter;

import jakarta.servlet.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

// in this every request je thread ma avse we set threadId for each thread
@Component
@Order(2)
public class ThreadIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("FILTER-> ThreadId Filter -> Prepprocessing in filter");
        long ThreadId=Thread.currentThread().getId();

        request.setAttribute("threadId",ThreadId);
        chain.doFilter(request,response);

        System.out.println("threadId"+ThreadId);
        System.out.println("FILTER-> ThreadId Filter -> PostProccessing in filter");

    }
}

//note
/*
and apde aa je threadId attribute ma set kariee chiee
toh ene controller ma use karbvu hoy toh
use this
public void check(HttpServletRequest request){
        System.out.println("threadId is"+request.getAttribute("threadId"));
    }
 */
