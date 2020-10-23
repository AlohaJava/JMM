package com.sech530;

public interface ThreadPool {

    void start(Runnable callback); // запускает потоки. Потоки бездействуют, до тех пор пока не появится новое задание в очереди (см. execute)

    void execute(Runnable runnable); // складывает это задание в очередь. Освободившийся поток должен выполнить это задание. Каждое задание должны быть выполнено ровно 1 раз

    int getCompletedTaskCount();

    int getFailedTaskCount();

    void interrupt();

    int getInterruptedTaskCount();

    boolean isFinished();
}