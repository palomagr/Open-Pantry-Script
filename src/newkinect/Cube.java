package newkinect;

import javafx.geometry.Point3D;

public class Cube {

    private final Point3D origin;
    private final Point3D max;
    private final double width;
    private final double length;
    private final double height;
    
    public Cube(Point3D origin, Point3D max) {
        this.origin = origin;
        this.max = max;
        width = Math.abs(this.max.getX() - this.origin.getX()); 
        length = Math.abs(this.max.getZ() - this.origin.getZ());
        height = Math.abs(this.max.getY() - this.origin.getY());
    }
    
    public Point3D origin() {
        return origin;
    }
    
    public Point3D max() {
        return max;
    }
    
    public boolean contains(Point3D point) {
        return (point.getX() >= origin.getX() && point.getX() <= max.getX()) &&
               (point.getY() >= origin.getY() && point.getY() <= max.getY()) &&
               (point.getZ() >= origin.getZ() && point.getZ() <= max.getZ());
    }
        
    public double width() {
        return width;
    }
    
    public double length() {
        return length;
    }
    
    public double height() {
        return height;
    }
    
}
