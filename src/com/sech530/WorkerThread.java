package com.sech530;

import java.util.concurrent.LinkedBlockingQueue;

class WorkerThread extends Thread {
    private final LinkedBlockingQueue<Runnable> queue;
    private final WorkerThreadListener workerThreadListener;
    private boolean isActive=true;
    public WorkerThread(WorkerThreadListener workerThreadListener,LinkedBlockingQueue<Runnable> queue) {
        this.queue=queue;
        this.workerThreadListener=workerThreadListener;
    }

    public boolean isActive() {
        return isActive;
    }

    public void isActive(boolean active) {
        isActive = active;
    }

    public void run() {
        Runnable task;

        while (isActive) {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    try {
                        queue.wait();
                    } catch (InterruptedException interruptedException) {
                        workerThreadListener.onTaskInterrupted(this);
                    }
                }
                task = queue.poll();
            }

            try {
                //System.out.println("Worker "+Thread.currentThread().getId()+" starting task!");
                task.run();
            } catch (RuntimeException e) {
                workerThreadListener.onTaskFailed(this);
                System.out.println("Error in runnable in thread: "+Thread.currentThread().getId());
            }
            finally {
                //System.out.println("Worker "+Thread.currentThread().getId()+" ended task!");
                workerThreadListener.onTaskComplited(this);
            }
        }

    }
}