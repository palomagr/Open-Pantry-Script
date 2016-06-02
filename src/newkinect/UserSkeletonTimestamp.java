package newkinect;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javafx.geometry.Point3D;

/**
 * A type representing the location of a user (given by a Skeleton object) at a given time.
 *
 */
public class UserSkeletonTimestamp {
    private Skeleton skeleton;
    private LocalDateTime dateTime;
    private String user;
    private double MAX_DISTANCE_TRAVELED_NEW_USER = 0.003;
    private double AVG_MILLISECONDS_BETWEEN_READS = 40;
    private double THRESHOLD_HEIGHT_DIFFERENCE = 0.1;
    
    // Abstraction Function:
    //      Takes a Skeleton object, a LocalDateTime object, and a String as a User ID and represents a user with this user ID detected at the location given by the Skeleton object at the time
    //      given by the LocalDateTime object.
    
    /**
     * Create a new UserSkeletonTimestamp object
     * @param user the User ID
     * @param skeleton the Skeleton of the user detected by the Kinect, giving at least the 13 joint locations needed to calculate the approximate height of the user
     * @param dateTime the time that the user is detected by the Kinect
     */
    public UserSkeletonTimestamp(String user, Skeleton skeleton, LocalDateTime dateTime) {
        this.user = user;
        this.skeleton = skeleton;
        this.dateTime = dateTime;
    }
    
    /**
     * Create a new UserSkeletonTimestamp object with the same user ID, Skeleton, and time attributes as another
     * @param original the other UserSkeletonTimestamp object
     */
    public UserSkeletonTimestamp(UserSkeletonTimestamp original) {
        this.user = original.getUserID();
        this.skeleton = original.getSkeleton();
        this.dateTime = original.getDateTime();
    }
    
    /**
     * Get the height of the Skeleton
     * @return the approximate height of the Skeleton in meters
     */
    public double getHeight() {
        return skeleton.getHeight();
    }
    
    /**
     * Get the Skeleton object
     * @return the Skeleton object
     */
    public Skeleton getSkeleton() {
        return new Skeleton(this.skeleton);
    }
    
    /**
     * Get the User ID
     * @return the User ID
     */
    public String getUserID() {
        return user;
    }
    
    /**
     * Get the time at which the user was detected
     * @return the time at which the user was detected
     */
    public LocalDateTime getDateTime() {
        return dateTime.minusNanos(0);
    }
    
    /**
     * Get the approximate general location of the user given by the coordinates of the middle of the spine of the user's skeleton
     * @return the approximate general location of the user in 3D space
     */
    public Point3D getLocation() {
        return skeleton.getLocation();
    }
    
    /**
     * Change the time of the UserSkeletonTimestamp to a new value
     * @param newTime the new value to replace the UserSkeletonTimestamp's original time value
     * @return a new UserSkeletonTimestamp with the same User ID and Skeleton values but with the newTime value for its time stamp
     */
    public UserSkeletonTimestamp updateTime(LocalDateTime newTime) {
        return new UserSkeletonTimestamp(this.user, new Skeleton(this.skeleton), newTime);
    }
    
    /**
     * Check if the user represented by a UserSkeletonTimestamp is the same user represented by another UserSkeletonTimestamp
     * @param other the other UserSkeletonTimestamp
     * @return True if the users represented by the two UserSkeletonTimestamp objects are very likely the same person, otherwise return False.
     */
    public boolean isSamePerson(UserSkeletonTimestamp other) {
        // calculate the distance between the two skeleton's represented in each UserSkeletonTimestamp
        double distanceBetween = this.getLocation().distance(other.getLocation());
        // calculate the time in milliseconds between the two times in each UserSkeletonTimestamp
        double timeBetween;
        if (this.getDateTime().isBefore(other.getDateTime())) {
            timeBetween = this.getDateTime().until(other.getDateTime(), ChronoUnit.MILLIS);
        } else {
            timeBetween = other.getDateTime().until(other.getDateTime(), ChronoUnit.MILLIS);
        }
        // If it seems reasonable to assume the two objects represent the same user determined by the distance traveled over the time between each reading and the heights of the skeletons, return True
        return (distanceBetween < MAX_DISTANCE_TRAVELED_NEW_USER && timeBetween < AVG_MILLISECONDS_BETWEEN_READS && (Math.abs(this.getHeight() - other.getHeight()) < THRESHOLD_HEIGHT_DIFFERENCE));
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
