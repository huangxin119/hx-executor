package org.example.demo.thread;

import org.apache.catalina.Executor;
import org.example.framework.queue.ResizeableCapacityLinkedBlockingQueue;
import org.example.framework.thread.HxExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadTaskBean {

    @Bean
    public HxExecutor calculateExecutor() {
        return new HxExecutor(
                "calculateExecutor",
                2, // 核心线程数
                2, // 最大线程数
                600, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new ResizeableCapacityLinkedBlockingQueue<>(10)//队列长度
        );
    }

    @Bean
    public HxExecutor ioExecutor() {
        return new HxExecutor(
                "ioExecutor",
                4, // 核心线程数
                8, // 最大线程数
                60, // 空闲线程存活时间
                TimeUnit.SECONDS, // 时间单位
                new ResizeableCapacityLinkedBlockingQueue<>(20)//队列长度
        );
    }

}
