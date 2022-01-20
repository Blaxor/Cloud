package ro.deiutzblaxo.cloud.responses;

public interface Task<R, W> {

    String getName();

    R getResult();

    void doWork(W work);

    void doWork(Runnable work);

    boolean isFinished();
}
