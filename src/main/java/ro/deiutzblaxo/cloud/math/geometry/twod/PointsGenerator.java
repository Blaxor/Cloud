package ro.deiutzblaxo.cloud.math.geometry.twod;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;
import ro.deiutzblaxo.cloud.utils.objects.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

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

    public static Point2D[] pointsForACircle(Point2D middle, float radius, int numberPoints) {

        Point2D[] points = new Point2D[numberPoints];
        new pointsForACircleThreadManager(points, new ArrayList<Pair<Integer, StructTaskPointsForACircle>>() {{
            for (int i = 0; i < numberPoints; i++) {
                add(new Pair<>(i, new StructTaskPointsForACircle(i, middle, radius)));
            }
        }}, 3);
        for (int i = 0; i < numberPoints; i++)
            points[i] = pointFromCircleRadius(middle, 380 / (i + 1), radius);
        return points;
    }

    protected static class pointsForACircleThreadManager {
        Point2D[] points;
        Queue<Pair<Integer, StructTaskPointsForACircle>> tasks = new PriorityQueue<>();
        Boolean[] done;
        ExecutorService factory;

        public pointsForACircleThreadManager(Point2D[] points, ArrayList<Pair<Integer, StructTaskPointsForACircle>> tasks, int threads) {
            factory = Executors.newFixedThreadPool(threads);
            this.points = points;
            tasks.forEach(integerStructTaskPointsForACirclePair -> this.tasks.add(integerStructTaskPointsForACirclePair));
            done = new Boolean[tasks.size()];
            startProcess();
            look();
        }

        public void startProcess() {
            while (!tasks.isEmpty()) {
                factory.execute(new Runnable() {
                    @Override
                    public void run() {
                        Pair<Integer, StructTaskPointsForACircle> value = tasks.poll();
                        StructTaskPointsForACircle data = value.getLast();
                        points[value.getLast().orderNumber] = pointFromCircleRadius(data.middle, 380 / (data.orderNumber + 1), data.radius);
                        done[value.getFirst()] = true;
                    }
                });

            }
        }

        public void look() {
            while (true) {
                if (Arrays.stream(tasks.toArray(done)).anyMatch(aBoolean -> aBoolean == false)) continue;
                break;
            }
        }
    }


    private static class StructTaskPointsForACircle {
        public int orderNumber;
        public Point2D middle;

        public float radius;

        public StructTaskPointsForACircle(int orderNumber, Point2D middle, float radius) {
            this.orderNumber = orderNumber;
            this.middle = middle;
            this.radius = radius;
        }
    }


}
