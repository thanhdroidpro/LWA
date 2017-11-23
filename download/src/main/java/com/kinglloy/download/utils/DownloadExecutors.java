package com.kinglloy.download.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinyalin
 * @since 2017/5/27.
 */

public class DownloadExecutors {
    private final static int DEFAULT_IDLE_SECOND = 5;

    public static ThreadPoolExecutor newDefaultThreadPool(int nThreads, String prefix) {
        return newDefaultThreadPool(nThreads, new LinkedBlockingQueue<Runnable>(), prefix);
    }

    public static ThreadPoolExecutor newDefaultThreadPool(int nThreads,
                                                          LinkedBlockingQueue<Runnable> queue,
                                                          String prefix) {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads,
                DEFAULT_IDLE_SECOND, TimeUnit.SECONDS, queue, new DownloadThreadFactory(prefix));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private static class DownloadThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        DownloadThreadFactory(String prefix) {
            group = Thread.currentThread().getThreadGroup();
            namePrefix = DownloadFileUtil.getThreadPoolName(prefix);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);

            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
