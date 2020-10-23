package com.sech530;

public class ExecutionManagerImp implements ExecutionManager {
    private static int FIXED_POOL_SIZE = 5;
    private final Context context;
    private final FixedThreadPool threadPool;

    public ExecutionManagerImp()  {
        threadPool=new FixedThreadPool(FIXED_POOL_SIZE);
        this.context = new ContextImp(threadPool);
    }

    public static int getFixedPoolSize() {
        return FIXED_POOL_SIZE;
    }

    public static void setFixedPoolSize(int fixedPoolSize) {
        if(fixedPoolSize<=0) throw new IllegalArgumentException("FIXED_POOL_SIZE >=0!");
        FIXED_POOL_SIZE = fixedPoolSize;
    }

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        for (Runnable task : tasks) {
            threadPool.execute(task);
        }
        threadPool.start(callback);
        return context;
    }
}