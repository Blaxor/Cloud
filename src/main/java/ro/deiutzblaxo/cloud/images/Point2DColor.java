package ro.deiutzblaxo.cloud.images;

import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;

import java.awt.*;

public class Point2DColor extends Point2D {
    public Color color;

    public Point2DColor(float x, float y, Color color) {
        super(x, y);
        this.color = color;

    }

    @Override
    public String toString() {
        return "Point2DColor{" +
                "color=" + color +
                ", X=" + X +
                ", Y=" + Y +
                '}';
    }
}
