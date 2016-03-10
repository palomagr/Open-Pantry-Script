package newkinect;

import javafx.geometry.Point3D;
import java.time.LocalDateTime;

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
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof PointInTime)) { return false; }
        else {
            PointInTime other = (PointInTime) o;
            return this.point.equals(other.getPoint()) && this.dateTime.equals(other.getDateTime());
        }
    }
    
    @Override
    public int hashCode() {
        return this.point.hashCode() + this.dateTime.hashCode();
    }
    
    @Override
    public String toString() {
        return point + " at time " + dateTime;
    }
}
