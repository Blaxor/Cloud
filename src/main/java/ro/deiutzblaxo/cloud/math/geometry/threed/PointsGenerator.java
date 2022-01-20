package ro.deiutzblaxo.cloud.math.geometry.threed;

import ro.deiutzblaxo.cloud.math.geometry.threed.objects.Point3D;

public class PointsGenerator {

    public static Point3D pointShellSphere(Point3D middle , float radius, float angle, float inclination){
        float x,y,z;
        x = (float) (middle.X + (radius * Math.cos(angle) * Math.sin(inclination)));
        y = (float) (middle.Y + (radius * Math.sin(angle) * Math.sin(inclination)));
        z = (float) (middle.Z + (radius* Math.cos(inclination)));

        return new Point3D(x,y,z);

    }

}
