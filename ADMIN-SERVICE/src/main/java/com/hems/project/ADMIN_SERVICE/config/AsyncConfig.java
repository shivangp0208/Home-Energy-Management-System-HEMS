package com.hems.project.ADMIN_SERVICE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean
    public TaskDecorator contextCopyingDecorator() {
        return runnable -> {
            RequestAttributes context = RequestContextHolder.getRequestAttributes();

            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(context);
                    runnable.run();
                } finally {
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        };
    }

    @Bean(name = "contextAwareExecutor")
    public Executor contextAwareExecutor(TaskDecorator contextCopyingDecorator) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);

        executor.setTaskDecorator(contextCopyingDecorator);

        executor.initialize();

        return executor;
    }
}