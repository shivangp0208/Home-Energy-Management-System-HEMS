package com.project.hems.SiteManagerService.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    //ahiya apde cross cutting concern lakshu
//JoinPoint represnet execution no point
    //* com.project.hems.SiteManagerService.service.*.*(..)) this is pointCut expression spseicific aa condition match thay tyare j Advice run thase
//    @Before("execution(* com.project.hems.SiteManagerService.service.*.*(..))")//advice kevi padse ke kyare aa ne chalavu che and ene kai method ni pehla chalavu ene PointCut ma kesu
//    public void logBeforeMethod(JoinPoint joinPoint){//aa method kaya point per chale che ene represent karva ahiya JoinPoint no object madi jase
//      log.info("Before method execution {}",joinPoint.getSignature().getName());
//    }
//
//
//    @After("execution(* com.project.hems.SiteManagerService.service.*.*(..))")
//    public void logAfterMethod(JoinPoint joinPoint){
//        log.info("After method execution {}",joinPoint.getSignature().getName());
//    }

    //around ma manually result return karvu padse
    @Around("execution(* com.project.hems.SiteManagerService.service.*.*(..))")
    public Object logAroundMethod(ProceedingJoinPoint joinPoint){
        //ProceedingJoinPoint is a child class of JoinPoint and ama apde .procced() method madse jena madad thi
        //apde kyare actual method run karavi che e kari sakiee..
        long startTime=System.currentTimeMillis();
        log.info("Aound after method execution {}" , joinPoint.getSignature().getName());

        try {
            Object result=joinPoint.proceed();//ahiya have method nu je actual logic hase e run thase
            return result;

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }finally {
            long endTime=System.currentTimeMillis();
            log.info("Time take {} ms", endTime-startTime);
            log.info("After before metod execution {} ",joinPoint.getSignature().getName());
        }

    }



}
