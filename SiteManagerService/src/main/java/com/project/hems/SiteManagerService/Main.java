package com.project.hems.SiteManagerService;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.function.Consumer;
import java.util.function.Supplier;

@EnableAsync
public class Main {
    //if we use static then class na ketla pan object bane badha same j copy ek j madse
    //and if we not use static then badha jode alag alag copy hase
    private static int a=10;
    @Async
    public static void main(String[] args) throws InterruptedException {
        Main a=new Main();
        a.a=20;
        Main b=new Main();
        System.out.println(a.a);
        System.out.println(b.a);

        Consumer<String> consume=(message)-> System.out.println(message);
        consume.accept("hy my name is jills");

        Supplier<String> supplier= ()-> "do nothing";
        supplier.get();
        asyncMethod();
        System.out.println("thread is "+ Thread
                .currentThread().getName());
    }

    @Async
    public static void asyncMethod() throws InterruptedException {
        Thread.sleep(4000);
        System.out.println("working thread is "+ Thread
                .currentThread().getName());
    }
}
