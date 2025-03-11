package org.example.framework.monitor;

import lombok.extern.slf4j.Slf4j;
import org.example.framework.thread.HxExecutor;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 上报监控数据线程任务
 */
@Component
@EnableScheduling
@Slf4j
public class MonitorExecutor {
    //上报间隔时间
    private static long FLASH_TIME = 10000L;

    //告警队列利用率
    private static double REPORT_RATE = 0.5d;

    @Resource
    private ApplicationContext applicationContext;

    @Scheduled(fixedRate = 10000L)
    public void checkAndReportData() {
        Map<String, HxExecutor> beans = applicationContext.getBeansOfType(HxExecutor.class);
        //依次上报
        for (Map.Entry<String, HxExecutor> entry : beans.entrySet()) {
            HxExecutor executor = entry.getValue();
            //todo这里可以上报到数据库实现持久化
            log.info(executor.toString());
            //todo校验是否需要告警,实现自定义告警策略
            BigDecimal rate = new BigDecimal(executor.getQueue().remainingCapacity())
                    .divide(new BigDecimal(executor.getQueue().size()).add(new BigDecimal(executor.getQueue().remainingCapacity())));
            if(rate.compareTo(new BigDecimal(REPORT_RATE))<0){
                log.error("阻塞队列存在任务阻塞情况，请检查线程池："+executor.getPoolName());
            }
        }
    }


}
