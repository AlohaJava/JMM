package com.sech530;

public interface WorkerThreadListener {
    void onTaskComplited(Thread thread);
    void onTaskFailed(Thread thread);
    void onTaskInterrupted(Thread thread);
}
