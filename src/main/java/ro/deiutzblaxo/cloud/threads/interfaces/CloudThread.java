package ro.deiutzblaxo.cloud.threads.interfaces;

import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;

public abstract class CloudThread<T> extends Thread {

    protected abstract void finish(CallBack<T> callback, T value);


    public abstract void run();
}
