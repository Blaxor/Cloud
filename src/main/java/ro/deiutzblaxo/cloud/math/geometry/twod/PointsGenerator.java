package ro.deiutzblaxo.cloud.math.geometry.twod;


import org.checkerframework.checker.units.qual.C;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.math.geometry.twod.shapes.Circle;
import ro.deiutzblaxo.cloud.threads.CircleThread;
import ro.deiutzblaxo.cloud.threads.interfaces.CallBack;
import ro.deiutzblaxo.cloud.threads.interfaces.CloudThread;
import ro.deiutzblaxo.cloud.utils.objects.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PointsGenerator {

    public static Point2D[] pointsBetween2Points2D(Point2D p1, Point2D p2, int quantity) {
        Point2D[] points = new Point2D[quantity];
        float ydiff = (p2.Y - p1.Y), xdiff = p2.X - p1.X;
        float slope = (p2.Y - p1.Y) / (p2.X - p1.X);
        float x, y;

        --quantity;

        for (float i = 0; i < quantity; i++) {
            y = slope == 0 ? 0 : ydiff * (i / quantity);
            x = slope == 0 ? xdiff * (i / quantity) : y / slope;
            points[(int) i] = new Point2D(x + p1.X, y + p1.Y);
        }

        points[quantity] = p2;
        return points;
    }

    public static Point2D pointFromCircleRadius(Point2D middle, float angle, float radius) {
        float x = (float) (middle.X + (radius * Math.cos(angle)));
        float y = (float) (middle.Y + (radius * Math.sin(angle)));
        return new Point2D(x, y);
    }

    public static void pointsForACircle(Point2D middle, float radius, int numberPoints, CallBack<Point2D[]> callback) {
        CloudThread thread = new CircleThread(middle, radius, numberPoints, callback);
        thread.start();

    }


}
