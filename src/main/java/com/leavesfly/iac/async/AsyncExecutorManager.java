package com.leavesfly.iac.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步任务执行器管理器
 * 
 * 提供不同类型的线程池和异步任务执行功能，支持优雅关闭
 * 采用单例模式确保资源的统一管理
 */
public final class AsyncExecutorManager {

    private static final AsyncExecutorManager INSTANCE = new AsyncExecutorManager();
    
    // 不同类型的线程池
    private final ExecutorService computeIntensivePool;
    private final ExecutorService ioIntensivePool;
    private final ScheduledExecutorService scheduledPool;
    private final ForkJoinPool forkJoinPool;
    
    private volatile boolean isShutdown = false;

    private AsyncExecutorManager() {
        // 计算密集型任务池 - 线程数 = CPU核心数
        this.computeIntensivePool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            createThreadFactory("compute-pool")
        );
        
        // I/O密集型任务池 - 线程数 = CPU核心数 * 2
        this.ioIntensivePool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2,
            createThreadFactory("io-pool")
        );
        
        // 定时任务池
        this.scheduledPool = Executors.newScheduledThreadPool(
            2,
            createThreadFactory("scheduled-pool")
        );
        
        // Fork/Join池 - 用于分治算法
        this.forkJoinPool = new ForkJoinPool(
            Runtime.getRuntime().availableProcessors(),
            ForkJoinPool.defaultForkJoinWorkerThreadFactory,
            null,
            true
        );
        
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public static AsyncExecutorManager getInstance() {
        return INSTANCE;
    }

    /**
     * 创建自定义线程工厂
     */
    private ThreadFactory createThreadFactory(String namePrefix) {
        return new ThreadFactory() {
            private final AtomicInteger counter = new AtomicInteger(0);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, namePrefix + "-" + counter.incrementAndGet());
                thread.setDaemon(true);
                thread.setUncaughtExceptionHandler((t, e) -> {
                    System.err.println("未捕获异常在线程 " + t.getName() + ": " + e.getMessage());
                    e.printStackTrace();
                });
                return thread;
            }
        };
    }

    /**
     * 执行计算密集型任务
     */
    public <T> CompletableFuture<T> executeCompute(java.util.function.Supplier<T> task) {
        checkShutdown();
        return CompletableFuture.supplyAsync(task, computeIntensivePool);
    }

    /**
     * 执行I/O密集型任务
     */
    public <T> CompletableFuture<T> executeIO(java.util.function.Supplier<T> task) {
        checkShutdown();
        return CompletableFuture.supplyAsync(task, ioIntensivePool);
    }

    /**
     * 执行定时任务
     */
    public CompletableFuture<Void> executeScheduled(Runnable task, long delay, TimeUnit unit) {
        checkShutdown();
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        scheduledPool.schedule(() -> {
            try {
                task.run();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay, unit);
        
        return future;
    }

    /**
     * 执行Fork/Join任务
     */
    public <T> CompletableFuture<T> executeForkJoin(java.util.concurrent.RecursiveTask<T> task) {
        checkShutdown();
        return CompletableFuture.supplyAsync(() -> forkJoinPool.invoke(task));
    }

    /**
     * 组合多个异步任务
     */
    public <T> CompletableFuture<T> combineAsync(
            CompletableFuture<?>... futures) {
        return CompletableFuture.allOf(futures)
            .thenApply(v -> null);
    }

    /**
     * 任意一个完成即返回
     */
    public <T> CompletableFuture<Object> anyOfAsync(
            CompletableFuture<?>... futures) {
        return CompletableFuture.anyOf(futures);
    }

    /**
     * 检查是否已关闭
     */
    private void checkShutdown() {
        if (isShutdown) {
            throw new IllegalStateException("AsyncExecutorManager已关闭");
        }
    }

    /**
     * 优雅关闭所有线程池
     */
    public void shutdown() {
        if (isShutdown) {
            return;
        }
        
        isShutdown = true;
        
        System.out.println("正在关闭异步执行器...");
        
        // 关闭所有线程池
        shutdownPool(computeIntensivePool, "计算密集型池");
        shutdownPool(ioIntensivePool, "I/O密集型池");
        shutdownPool(scheduledPool, "定时任务池");
        
        // Fork/Join池特殊处理
        if (!forkJoinPool.isShutdown()) {
            forkJoinPool.shutdown();
            try {
                if (!forkJoinPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    forkJoinPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                forkJoinPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("异步执行器已关闭");
    }

    /**
     * 关闭指定线程池
     */
    private void shutdownPool(ExecutorService pool, String poolName) {
        pool.shutdown();
        try {
            if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println(poolName + "未能在5秒内关闭，强制关闭...");
                pool.shutdownNow();
                if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println(poolName + "未能关闭");
                }
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}