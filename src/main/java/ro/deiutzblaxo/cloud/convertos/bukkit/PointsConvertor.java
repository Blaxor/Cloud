package ro.deiutzblaxo.cloud.convertos.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import ro.deiutzblaxo.cloud.math.geometry.threed.objects.Point3D;
import ro.deiutzblaxo.cloud.math.geometry.twod.objects.Point2D;

public class PointsConvertor {

    public Point2D locationToPoint2D(Location location) {
        return new Point2D((float) location.getX(), (float) location.getZ());
    }

    public Point3D locationToPoint3D(Location location) {
        return new Point3D((float) location.getX(), (float) location.getY(), (float) location.getZ());
    }

    public Location point2DToLocation(Point2D point2D, World world, double y) {
        return new Location(world, point2D.X, y, point2D.Y);
    }

    public Location point3DToLocation(Point3D point3D, World world) {
        return new Location(world, point3D.X, point3D.Y, point3D.Z);
    }
}
