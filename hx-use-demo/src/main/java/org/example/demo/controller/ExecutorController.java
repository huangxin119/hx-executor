package org.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.framework.thread.HxExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

@RestController
@Slf4j
public class ExecutorController {

    @Autowired
    private HxExecutor calculateExecutor;

    @Autowired
    private HxExecutor ioExecutor;



    @RequestMapping("/io")
    public String io() {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("io任务已完成");
            }
        });
        return "ok";
    }


    @RequestMapping("/calculate")
    public String calculate() {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                log.info("calculate任务已完成");
            }
        });
        return "ok";
    }


    @RequestMapping("/executeException")
    public String executeException() {
        ioExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int i = 1 / 0;
                log.info("executeException任务已完成");
            }
        });
        return "ok";
    }

    @RequestMapping("/submitException")
    public String submitException() {
        Future<Integer> task = ioExecutor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int i = 1 / 0;
                return i;
            }
        });
        return task.toString();
    }

}
