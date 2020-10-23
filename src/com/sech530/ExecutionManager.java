package com.sech530;

public interface ExecutionManager {
    Context execute(Runnable callback, Runnable... tasks);
}