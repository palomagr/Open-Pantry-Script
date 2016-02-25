package newkinect;

import javafx.geometry.Point3D;
import java.time.LocalDateTime;
import java.util.Collection;

public class PointInTime {
    private Point3D point;
    private LocalDateTime dateTime;
    
    public PointInTime(Point3D point, LocalDateTime dateTime) {
        this.point = point;
        this.dateTime = dateTime;
    }
    
    public Point3D getPoint() {
        return new Point3D(point.getX(),point.getY(),point.getZ());
    }
    
    public LocalDateTime getDateTime() {
        return dateTime.minusNanos(0);
    }
    
    public String toString() {
        return point + " at " + dateTime;
    }
}
