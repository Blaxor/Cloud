package ro.deiutzblaxo.cloud.math.geometry.twod.shapes;

import org.bukkit.event.Listener;
import ro.deiutzblaxo.cloud.math.geometry.threed.objects.Point3D;
import ro.deiutzblaxo.cloud.math.geometry.twod.PointsGenerator;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;

import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Circle {
    //INFO ABOUT CIRCLE
    private Point2D[] points;
    float radius, totalPoints, angleDiff;
    private Point2D middle;


    public Circle(Point2D middle, float radius, int totalPoints) {
        this.radius = radius;
        this.middle = middle;
        this.totalPoints = totalPoints;
        this.points = new Point2D[totalPoints];
        angleDiff = 360 / totalPoints;
        for (int i = 0; i < points.length; i++) {
            points[i] = PointsGenerator.pointFromCircleRadius(middle, angleDiff * i, radius);
        }
    }

    public Point2D[] getPoints() {
        return points;
    }
}
