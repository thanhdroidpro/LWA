package com.kinglloy.download.schedule;

import android.os.HandlerThread;
import android.os.Process;

/**
 * A {@link HandlerThread} with a specified process priority.
 *
 * @author jinyalin
 * @since 2017/5/26.
 */
public final class PriorityHandlerThread extends HandlerThread {
    private final int priority;

    public PriorityHandlerThread(String name, int priority) {
        super(name, priority);
        this.priority = priority;
    }

    @Override
    public void run() {
        Process.setThreadPriority(priority);
        super.run();
    }
}
