package org.example.framework.thread;

import lombok.extern.slf4j.Slf4j;
import org.example.framework.queue.ResizeableCapacity;

import java.util.Date;
import java.util.concurrent.*;

@Slf4j
public class HxExecutor extends ThreadPoolExecutor {

    private String poolName;


    // 保存任务开始执行的时间，当任务结束时，用任务结束时间减去开始时间计算任务执行时间
    private ConcurrentHashMap<String, Date> startTimes;


    //下面是一些默认参数
    private static final RejectedExecutionHandler defaultHandler =
            new AbortPolicy();


    //构造方法归一到最下面的
    public HxExecutor(String poolName,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this(poolName,corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,Executors.defaultThreadFactory(),defaultHandler);
    }

    public HxExecutor(String poolName,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this(poolName,corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,defaultHandler);
    }

    public HxExecutor(String poolName,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        this(poolName,corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,Executors.defaultThreadFactory(), handler);
    }

    public HxExecutor(String poolName,int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.poolName = poolName;
        startTimes = new ConcurrentHashMap<>();
    }


    @Override
    public String toString() {
        StringBuffer str = new StringBuffer();
        str.append("HxExecutor{poolName=").append(poolName).append(",");
        str.append("corePoolSize=").append(getCorePoolSize()).append(",");
        str.append("maximumPoolSize=").append(getMaximumPoolSize()).append(",");
        str.append("activeCount=").append(getActiveCount()).append(",");
        str.append("queueCapacity=").append(getQueue().size()).append(",");
        str.append("queueRemainingCapacity=").append(getQueue().remainingCapacity()).append(",");
        str.append("completedTaskCount=").append(getCompletedTaskCount()).append(",");

        return str.toString();
    }

    public String getPoolName() {
        return poolName;
    }

    public void setPoolName(String poolName) {
        this.poolName = poolName;
    }

    /**
     * 修改队列长度接口，必须要是ResizeableCapacity才支持该功能
     * @param capacity
     * @return
     */
    public boolean setCapacity(int capacity){
        BlockingQueue queue = getQueue();
        if(queue instanceof ResizeableCapacity){
            ((ResizeableCapacity) queue).setCapacity(capacity);
            return true;
        }
        System.out.println("暂不支持该功能");
        return false;
    }

    public int getCapacity(){
        return getQueue().size();
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        startTimes.put(String.valueOf(r.hashCode()), new Date());
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        Date startDate = startTimes.remove(String.valueOf(r.hashCode()));
        Date finishDate = new Date();
        long diff = finishDate.getTime() - startDate.getTime();
        log.info("任务"+String.valueOf(r.hashCode())+"执行"+diff+"ms");
        super.afterExecute(r, t);

        // 如果任务执行过程中抛出了异常
        if (t != null) {
            System.err.println("任务执行过程中抛出异常: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        // 如果任务是 FutureTask，异常会在future中，检查其执行状态
        if (r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                future.get(); // 获取任务结果，如果任务抛出异常，这里会捕获
            } catch (Exception e) {
                System.err.println("任务异常: " + e.getMessage());
                e.printStackTrace();
            }
        }

    }
}
