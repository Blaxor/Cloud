package ro.deiutzblaxo.cloud.threads;

import ro.deiutzblaxo.cloud.math.geometry.twod.PointsGenerator;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;
import ro.deiutzblaxo.cloud.threads.interfaces.CloudThread;

public class CircleThread extends CloudThread<Point2D[]> {
    float radius, angle;
    int numberPoints;
    Point2D middle;
    Point2D[] points;
    CallBack<Point2D[]> callback;

    public CircleThread(Point2D middle, float radius, int numberPoints, CallBack<Point2D[]> template) {
        this.middle = middle;
        this.radius = radius;
        this.numberPoints = numberPoints;
        points = new Point2D[numberPoints];
        angle = 360 / numberPoints;
        this.callback = template;
    }

    @Override
    public void finish(CallBack<Point2D[]> callback, Point2D[] value) {
        callback.finished(value);
        this.interrupt();
    }

    @Override
    public void run() {
        for (int i = 0; i < numberPoints; i++) {
            points[i] = PointsGenerator.pointFromCircleRadius(middle, angle * i, radius);
        }
        finish(callback, points);


    }
}
