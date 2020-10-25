package com.sech530.Task;

import java.util.concurrent.Callable;

public class Task<T> {
    private final Object lock = new Object();
    private volatile RuntimeException exceptionInCallable;
    private volatile T result;
    private final Callable<T> callable;

    public Task(Callable<T> callable) {
        this.callable = callable;
    }

    public T get() {

        if (exceptionInCallable != null) {
            throw exceptionInCallable;
        }

        if (result != null) {
            return result;
        }

        synchronized (lock) {
            if (exceptionInCallable == null && result == null) {
                try {
                    result = callable.call();
                } catch (Exception e) {
                    exceptionInCallable = new TaskException(e);
                    throw exceptionInCallable;
                }
            } else if (exceptionInCallable != null) {
                throw exceptionInCallable;
            }
        }

        return result;
    }
}