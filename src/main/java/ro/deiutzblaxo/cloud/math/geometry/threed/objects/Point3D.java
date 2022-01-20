package ro.deiutzblaxo.cloud.math.geometry.threed.objects;

public class Point3D {

    public float X;
    public float Y;
    public float Z;

    public Point3D(float x, float y,float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "X=" + X +
                ", Y=" + Y +
                ", Z=" + Z +
                '}';
    }
}
