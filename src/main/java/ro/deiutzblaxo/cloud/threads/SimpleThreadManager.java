package ro.deiutzblaxo.cloud.threads;

import ro.deiutzblaxo.cloud.utils.CloudLogger;

import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

public class SimpleThreadManager implements ThreadFactory {
    private final int maxThreads;
    private ArrayList<Thread> threads;

    public SimpleThreadManager(int maxThreads) {
        threads = new ArrayList<>(maxThreads);
        this.maxThreads = maxThreads;
    }

    public void closeManager() {
        CloudLogger.getLogger().log(Level.INFO, "Closing simple thread manager");
        threads.forEach(Thread::interrupt);
        CloudLogger.getLogger().log(Level.INFO, "Closed simple thread manager");
    }


    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        threads.add(thread);
        thread.start();
        return thread;
    }
}
