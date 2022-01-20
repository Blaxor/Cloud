package ro.deiutzblaxo.cloud.math.geometry.twod.objects;


public class Point2D {

    public float X;
    public float Y;

    public Point2D(float x, float y) {
        this.X = x;
        this.Y = y;
    }

    @Override
    public String toString() {
        return "Point2D{" +
                "X=" + X +
                ", Y=" + Y +
                '}';
    }

    public double distance(Point2D to) {
        return distance(this, to);
    }

    public static double distance(Point2D from, Point2D to) {
        return Math.sqrt(Math.pow(to.X - from.X, 2) + Math.pow(to.Y - from.Y, 2));
    }
}
