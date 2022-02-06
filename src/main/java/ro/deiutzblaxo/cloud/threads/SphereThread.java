package ro.deiutzblaxo.cloud.threads;

import ro.deiutzblaxo.cloud.math.geometry.threed.objects.Point3D;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;
import ro.deiutzblaxo.cloud.threads.interfaces.CloudThread;

public class SphereThread extends CloudThread<Point3D[]> {

    private float radius, number, angleDiff, inclinationDiff;
    private Point3D middle;

    public SphereThread(Point3D middle, float radius, float number, float angleDiff, float inclinationDiff) {
        this.radius = radius;
        this.number = number;
        this.angleDiff = angleDiff;
        this.inclinationDiff = inclinationDiff;
        this.middle = middle;
    }

    @Override
    protected void finish(CallBack<Point3D[]> callback, Point3D[] value) {

    }

    @Override
    public void run() {

    }
}
