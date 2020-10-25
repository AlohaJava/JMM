package com.sech530;

import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool implements ThreadPool, WorkerThreadListener {

    private final Object lock = new Object();
    private final WorkerThread[] workers;
    private final LinkedBlockingQueue<Runnable> queue;
    private volatile int size;
    private volatile int complitedTaskCount = 0;
    private volatile int failedTaskCount = 0;
    private volatile int interruptedTaskCount = 0;
    private CountUpAndDownLatch tasksLatch;
    private Runnable callback;
    private boolean callbackCalled = false;
    private boolean isStarted = false;

    public FixedThreadPool(int size) {
        queue = new LinkedBlockingQueue<>();
        workers = new WorkerThread[size];
        this.size = size;
        for (int i = 0; i < size; i++) {
            workers[i] = new WorkerThread(this, queue);
        }
    }

    public int getSize() {
        return size;
    }

    @Override
    public void start(Runnable callback) {
        isStarted = true;
        tasksLatch = new CountUpAndDownLatch(queue.size());
        this.callback = callback;
        for (WorkerThread worker : workers) {
            worker.start();
        }
    }

    @Override
    public void execute(Runnable task) {
        synchronized (queue) {
            if (isStarted) {
                tasksLatch.countUp();
            }
            queue.add(task);
            queue.notify();
        }
    }

    @Override
    public int getCompletedTaskCount() {
        return complitedTaskCount;
    }

    @Override
    public int getFailedTaskCount() {
        return failedTaskCount;
    }

    @Override
    public int getInterruptedTaskCount() {
        return interruptedTaskCount;
    }

    @Override
    public void interrupt() {
        synchronized (queue) {
            interruptedTaskCount += queue.size();
            queue.clear();
            queue.notifyAll();
        }
    }


    @Override
    public boolean isFinished() {
        synchronized (lock) {
            if (tasksLatch.getCount() == 0) {
                callEndTask();
                return true;
            }
            return false;
        }
    }


    private synchronized void callEndTask() {
        if (!callbackCalled) {
            callbackCalled = true;
            callback.run();
        }
    }


    private void countDownAndCheckFinish() throws InterruptedException {
        tasksLatch.countDownOrWaitIfZero();
        isFinished();
    }

    @Override
    public void onTaskEvent(WorkerThreadEvent event) {
        synchronized (lock) {
            switch (event) {
                case FAILED:
                    ++failedTaskCount;
                    break;
                case COMPLITED:
                    ++complitedTaskCount;
                    break;
                case INTERRUPTED:
                    ++interruptedTaskCount;
                    break;
                default:
                    System.out.println("NOT Implemented event:" + event + " in FixedThredPool.class");
            }
            try {
                countDownAndCheckFinish();
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }
}