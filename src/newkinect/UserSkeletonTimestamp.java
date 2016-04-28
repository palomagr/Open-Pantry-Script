package newkinect;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.geometry.Point3D;

public class UserSkeletonTimestamp {
    private Skeleton skeleton;
    private LocalDateTime dateTime;
    private String user;
    
    public UserSkeletonTimestamp(String user, Skeleton skeleton, LocalDateTime dateTime) {
        this.user = user;
        this.skeleton = skeleton;
        this.dateTime = dateTime;
    }
    
    public UserSkeletonTimestamp(UserSkeletonTimestamp original) {
        this.user = original.getUserID();
        this.skeleton = original.getSkeleton();
        this.dateTime = original.getDateTime();
    }
    
    public double getHeight() {
        return skeleton.getHeight();
    }
    
    public Skeleton getSkeleton() {
        return new Skeleton(this.skeleton);
    }
    
    public String getUserID() {
        return user;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime.minusNanos(0);
    }
    
    public Point3D getLocation() {
        return skeleton.getSpineMid();
    }
    
    public UserSkeletonTimestamp updateTime(LocalDateTime newTime) {
        return new UserSkeletonTimestamp(this.user, this.skeleton, newTime);
    }
    
    public boolean isSamePerson(UserSkeletonTimestamp other) {
        double distanceBetween = this.getLocation().distance(other.getLocation());
        double timeBetween = this.getDateTime().until(other.getDateTime(), ChronoUnit.MILLIS);
        double speed = distanceBetween / timeBetween;
        return (distanceBetween < 0.0006 && timeBetween < 40 && (Math.abs(this.getHeight() - other.getHeight()) < 0.1));
    }
    
    @Override
    public String toString() {
        return "(user: " + user + " height: " + skeleton.getHeight() + "; locat: " + skeleton.getLocation() + "\t at: " + dateTime + ")";
    }
    
    @Override
    public boolean equals(Object thatObject) {
        if ( !(thatObject instanceof UserSkeletonTimestamp)) { return false; }
        UserSkeletonTimestamp that = (UserSkeletonTimestamp)thatObject;
        
        return ((that.dateTime.isEqual(this.dateTime)) && this.getLocation().equals(that.getLocation()));
        
    }
}
