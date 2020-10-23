package com.sech530;

import java.util.concurrent.LinkedBlockingQueue;

public class FixedThreadPool implements ThreadPool, WorkerThreadListener {

    private final WorkerThread[] workers;
    private final LinkedBlockingQueue<Runnable> queue;
    private int size;
    private final Object lock = new Object();
    private int complitedTaskCount = 0;
    private int failedTaskCount = 0;
    private int interruptedTaskCount = 0;

    private Runnable callback;
    private boolean callbackCalled=false;

    public FixedThreadPool(int size) {
        queue = new LinkedBlockingQueue<Runnable>();
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
        this.callback = callback;
        for (WorkerThread worker : workers) {
            worker.start();
        }
    }

    @Override
    public void execute(Runnable task) {
        synchronized (queue) {
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
    public void interrupt() {
        synchronized (queue) {
            interruptedTaskCount += queue.size();
            queue.clear();
        }
    }

    @Override
    public int getInterruptedTaskCount() {
        return interruptedTaskCount;
    }

    @Override
    public boolean isFinished() {
        for (WorkerThread worker : workers) {
            if (worker.getState() == Thread.State.RUNNABLE || worker.getState() == Thread.State.TIMED_WAITING) {
                return false;
            }
        }
        callEndTask();
        return true;
    }


    private void callEndTask(){
        if(!callbackCalled){
            callbackCalled=true;
            callback.run();
        }
    }

    @Override
    public void onTaskComplited(Thread thread) {
        synchronized (lock) {
            complitedTaskCount++;
        }
    }

    @Override
    public void onTaskFailed(Thread thread) {
        synchronized (lock) {
            failedTaskCount++;
        }
    }

    @Override
    public void onTaskInterrupted(Thread thread) {
        synchronized (lock) {
            interruptedTaskCount++;
        }
    }

}